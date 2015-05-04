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
	private InterferenceVisitor interferenceVisitor = new InterferenceVisitor();
	private Map<String, Value> allocationMap = new HashMap<String, Value>();
	private Map<String, Value> tempAllocationMap = new HashMap<String, Value>();
	private Set<String> removedNodes = new HashSet<String>();

	public Map<String, Value> allocateRegisters(FunctionDeclaration func, int k)
	{
		liveness.clear();
		allocationMap.clear();
		interferenceVisitor.clear();
		removedNodes.clear();
		stackSlots = 0;
		spilled = 0;

		FlowGraph cfg = ControlFlowGraphBuilder.getCfg(func.getStatements());

		cfg.getExit().accept(liveness);

		interferenceVisitor.setLivenessMap(liveness.getLivenessMap());
		cfg.getExit().accept(interferenceVisitor);
		InterferenceGraph originalGraph = interferenceVisitor.getInterferenceGraph();
		for (Identifier param : func.getParams())
			originalGraph.removeNode(param.getId());

		Stack<String> stack = new Stack<String>();

		boolean complete = false;
		

		while (!complete)
		{
			tempAllocationMap.clear();
			tempAllocationMap.putAll(allocationMap);			
			registerCount = 0;
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
			}

			if (stack.isEmpty())
			{
				complete = true;
			}
			else
			{
				String nodeToColor;
				while (!stack.isEmpty())
				{
					nodeToColor = stack.pop();
					int color = assignColor(originalGraph, nodeToColor, k);
					if (color == -1)
					{
						spill(nodeToColor);
						complete = false;
						break;
					}
					else
					{
						complete = true;
					}
				}
			}
		}

		allocationMap.putAll(tempAllocationMap);		
		return allocationMap;
	}

	private void spill(String id)
	{
		allocationMap.put(id, new StackOffset(stackSlots++));
		removedNodes.add(id);
		spilled++;
	}

	private int assignColor(InterferenceGraph graph, String id, int k)
	{
		int color = -1;
		
		int i;
		for (i = 0; i < k; i++)
		{
			boolean sharesColor = false;
			for (String neighbor : graph.getNeighbors(id))
			{
				Value value = tempAllocationMap.get(neighbor);
				if (value instanceof Register && ((Register) value).getValue() == i)
				{
					sharesColor = true;
					break;
				}
			}
			if (!sharesColor)
			{
				color = i;
				break;
			}			
		}

		if (color != -1)
		{
			tempAllocationMap.put(id, new Register(color));
			if (color >= registerCount)
				registerCount = color + 1;
		}
		return color;
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
