package ir.regalloc;

import ir.backend.Location;
import ir.cfgraph.ControlFlowGraphBuilder;
import ir.cfgraph.FlowGraph;
import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class GraphColorAllocator
{
	public Map<Identifier, Location> allocateRegisters(FunctionDeclaration func,
			int k)
	{
		LivenessVisitor livenessVisitor = new LivenessVisitor();
		InterferenceVisitor interferenceVisitor = new InterferenceVisitor();

		FlowGraph flowGraph = ControlFlowGraphBuilder.getCfg(func.getStatements());

		livenessVisitor.setFunctionDeclaration(func);
		flowGraph.getExit().accept(livenessVisitor);

		int tempCount = func.getLocals().size() + func.getTemporaries().size();
		interferenceVisitor.setTemporaryCount(tempCount);
		flowGraph.getExit().accept(interferenceVisitor);

		InterferenceGraph graph = interferenceVisitor.getInterferenceGraph();

		Stack<Entry<Integer, Set<Integer>>> nodeStack = new Stack<Entry<Integer, Set<Integer>>>();
		Set<Entry<Integer, Set<Integer>>> spillCandidates = new HashSet<Entry<Integer, Set<Integer>>>();
		List<Entry<Integer, Set<Integer>>> sortedSpillCandidates = new ArrayList<Entry<Integer, Set<Integer>>>();
		do
		{
			List<Entry<Integer, Set<Integer>>> nodeList = graph.getEntryList();
			Collections.sort(nodeList,
					new Comparator<Entry<Integer, Set<Integer>>>()
					{
						@Override
						public int compare(Entry<Integer, Set<Integer>> o1,
								Entry<Integer, Set<Integer>> o2)
						{
							return o1.getKey() - o2.getKey();
						}
					});

			for (Entry<Integer, Set<Integer>> node : graph.getEntryList())
			{
				nodeStack.add(node);
				graph.removeNode(node.getKey());

				if (k >= node.getValue().size())
					spillCandidates.add(node);
			}
			if (!spillCandidates.isEmpty())
			{
				Collections.sort(nodeList,
						new Comparator<Entry<Integer, Set<Integer>>>()
						{
							@Override
							public int compare(Entry<Integer, Set<Integer>> o1,
									Entry<Integer, Set<Integer>> o2)
							{
								return o2.getKey() - o1.getKey();
							}
						});
				
			}

		} while (!spillCandidates.isEmpty());
	}
}
