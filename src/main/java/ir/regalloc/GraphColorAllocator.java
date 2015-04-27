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
	
	public Map<String, Value> allocateRegisters(FunctionDeclaration func,
			int k)
	{
		stackSlots = 0;
		spilled = 0;
		
		FlowGraph cfg = ControlFlowGraphBuilder.getCfg(func.getStatements());
		LivenessVisitor liveness = new LivenessVisitor();
		
		HashSet<String> ids = new HashSet<String>();
		for(Identifier id : func.getLocals())
			ids.add(id.getId());
		for(Identifier id : func.getTemporaries())
			ids.add(id.getId());
		for(Identifier id : func.getParams())
			liveness.addIgnoredId(id.getId());
		
				
		HashSet<String> spillCandidates = new HashSet<String>();
		Stack<String> removedNodes = new Stack<String>();
		Map<String, Value> allocationMap = new HashMap<String, Value>();
		InterferenceVisitor interferenceVisitor = new InterferenceVisitor();
		do
		{
			registerCount = 0;
			spillCandidates.clear();
			removedNodes.clear();
			liveness.clear();
			interferenceVisitor.clear();
			cfg.getExit().accept(liveness);
			
			interferenceVisitor.clear();
			interferenceVisitor.setLivenessMap(liveness.getLivenessMap());			
			cfg.getExit().accept(interferenceVisitor);
			
			
			InterferenceGraph graph = interferenceVisitor.getInterferenceGraph();
			List<Entry<String, Set<String>>> graphEntries = graph.getEntryList();
			Collections.sort(graphEntries, new Comparator<Entry<String, Set<String>>>()
					{
						@Override
						public int compare(Entry<String, Set<String>> o1,
								Entry<String, Set<String>> o2)
						{
					
							return o1.getValue().size() - o2.getValue().size();
						}
				
					});
			
			for(Entry<String, Set<String>> entry : graphEntries)
			{
				removedNodes.add(entry.getKey());
				ids.remove(entry.getKey());
				if(entry.getValue().size() >= k)
				{
					spillCandidates.add(entry.getKey());					
				}					
			}
			
			if(!spillCandidates.isEmpty())
			{
				String removeId = graphEntries.get(graphEntries.size() -1 ).getKey();
				allocationMap.put(removeId, new StackOffset(stackSlots++ * WORD_SIZE));
				liveness.addIgnoredId(removeId);	
				spilled++;
			}
			else
			{
				int i = 0;
				for(String id : removedNodes)
				{
					allocationMap.put(id, new Register(i++));
					if(i == k)
						i = 0;
					if( i > registerCount)
						registerCount = i;
				}
			}
		}
		while(!spillCandidates.isEmpty());
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
