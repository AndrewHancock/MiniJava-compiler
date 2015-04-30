package ir.regalloc;

import ir.cfgraph.ControlFlowGraphBuilder;
import ir.cfgraph.FlowGraph;
import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class GraphColorAllocator implements RegisterAllocator
{
	final static int WORD_SIZE = 8;
	private int stackSlots;
	private int spilled;
	private int registerCount;

	private LivenessVisitor liveness = new LivenessVisitor();
	InterferenceVisitor interferenceVisitor = new InterferenceVisitor();

	public Map<String, Value> allocateRegisters(FunctionDeclaration func, int k)
	{
		stackSlots = 0;
		spilled = 0;

		FlowGraph cfg = ControlFlowGraphBuilder.getCfg(func.getStatements());
		liveness.clear();
		cfg.getExit().accept(liveness);
		interferenceVisitor.clear();
		interferenceVisitor.setLivenessMap(liveness.getLivenessMap());
		cfg.getExit().accept(interferenceVisitor);
		InterferenceGraph originalGraph = interferenceVisitor.getInterferenceGraph();
		for(Identifier param : func.getParams())
			originalGraph.removeNode(param.getId());

		HashSet<String> spillCandidates = new HashSet<String>();
		Stack<String> stack = new Stack<String>();
		Map<String, Value> allocationMap = new HashMap<String, Value>();
		Set<String> removedNodes = new HashSet<String>();
		do
		{
			registerCount = 0;
			spillCandidates.clear();
			stack.clear();

			InterferenceGraph graph = originalGraph.deepCopy();
			for (String label : removedNodes)
				graph.removeNode(label);

			// Sort by number of neighbors, descending
			List<Entry<String, Set<String>>> graphEntries = graph.getEntryList();
			Collections.sort(graphEntries,
					new Comparator<Entry<String, Set<String>>>()
					{
						@Override
						public int compare(Entry<String, Set<String>> o1,
								Entry<String, Set<String>> o2)
						{

							return o2.getValue().size() - o1.getValue().size();
						}

					});

			for (Entry<String, Set<String>> entry : graphEntries)
			{
				stack.add(entry.getKey());
				if (entry.getValue().size() >= k)
				{
					spillCandidates.add(entry.getKey());
				}
			}

			if (!spillCandidates.isEmpty())
			{
				int neighborCount = 0;
				String removeLabel = null;
				for(String candidate : spillCandidates)
				{
					if(graph.getNeighbors(candidate).size() > neighborCount)
					{
						removeLabel = candidate;
						neighborCount = graph.getNeighbors(candidate).size();
					}
					
				}
				allocationMap.put(removeLabel,
						new StackOffset(stackSlots++));
				removedNodes.add(removeLabel);
				spilled++;
			}
			else
			{
				String nodeToColor = null;
				while(!stack.isEmpty())
				{
					nodeToColor = stack.pop();
					int color = -1;
					boolean sharesColor = false;
					for (int i = 0; i < k; i++)					
					{	
						color = i;
						for (String neighbor : originalGraph.getNeighbors(nodeToColor))
						{
							Value value = allocationMap.get(neighbor);
							if(value instanceof Register && ((Register)value).getValue() == i)
							{
								sharesColor = true;
								break;
							}
						}	
						if(!sharesColor)
							break;
						sharesColor = false;
					}
					
					if(!sharesColor)
					{
						allocationMap.put(nodeToColor, new Register(color));
					}

				}
			}
		} while (!spillCandidates.isEmpty());
		return allocationMap;
	}

	@Override
	public int getStackSize()
	{
		return stackSlots * WORD_SIZE;
	}

	@Override
	public int getSpillCount()
	{
		return spilled;
	}

	@Override
	public int getNumRegistersUsed()
	{
		return registerCount;
	}
}
