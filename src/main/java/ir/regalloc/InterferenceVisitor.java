package ir.regalloc;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.LinearCodePoint;

import java.util.HashSet;

public class InterferenceVisitor extends BottomUpVisitor
{

	private InterferenceGraph graph = new InterferenceGraph();
	
	public InterferenceVisitor()
	{
				
	}

	private void handleLivenessSet(HashSet<String> liveSet)
	{				
		for(String label: liveSet)
		{			
			graph.addOrGetNode(label);
			for(String neighbor : liveSet)
			{				
				if(!label.equals(neighbor))
					graph.addEdge(label, neighbor);				
			}
		}
	}
	
	@Override
	public void visit(BranchCodePoint codePoint)
	{
		handleLivenessSet(codePoint.getLiveSet());
		super.visit(codePoint);
	}

	@Override
	public void visit(LinearCodePoint codePoint)
	{
		handleLivenessSet(codePoint.getLiveSet());
		super.visit(codePoint);
	}
	
	public InterferenceGraph getInterferenceGraph()
	{
		return graph;
	}
}
