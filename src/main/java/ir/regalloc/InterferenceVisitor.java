package ir.regalloc;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.LinearCodePoint;

import java.util.BitSet;
import java.util.HashSet;

public class InterferenceVisitor extends BottomUpVisitor
{

	private InterferenceGraph graph = new InterferenceGraph();
	
	public InterferenceVisitor()
	{
				
	}
	
	public void setTemporaryCount(int temporaryCount)
	{
		
	}
	
	private HashSet<Integer> liveSet = new HashSet<Integer>();
	private void handleLivenessSet(BitSet liveness)
	{				
		for(int i = 0; i < liveness.size() - 1; i++ )
		{
			if(liveness.get(i))
				liveSet.add(i);			
		}
		
		for(Integer index: liveSet)
		{
			graph.addNode(index);
			for(Integer neighbor : liveSet)
			{
				graph.addEdge(index, neighbor);				
			}
		}
	}
	
	@Override
	public void visit(BranchCodePoint codePoint)
	{
		handleLivenessSet(codePoint.getLiveness());
		super.visit(codePoint);
	}

	@Override
	public void visit(LinearCodePoint codePoint)
	{
		handleLivenessSet(codePoint.getLiveness());
		super.visit(codePoint);
	}
	
	public InterferenceGraph getInterferenceGraph()
	{
		return graph;
	}
}
