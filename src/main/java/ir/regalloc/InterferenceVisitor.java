package ir.regalloc;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.CodePoint;
import ir.cfgraph.LinearCodePoint;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InterferenceVisitor extends BottomUpVisitor
{

	private InterferenceGraph graph = new InterferenceGraph();
	private Map<CodePoint, Set<String>>livenessMap; 
	public InterferenceVisitor()
	{
				
	}
	
	@Override
	public void clear()
	{
		super.clear();
		graph.clear();
		livenessMap = null;
	}
	
	
	public void setLivenessMap(Map<CodePoint, Set<String>> livenessMap)
	{
		this.livenessMap = livenessMap;
	}

	private void handleLivenessSet(Set<String> liveSet)
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
		handleLivenessSet(livenessMap.get(codePoint));
		super.visit(codePoint);
	}

	@Override
	public void visit(LinearCodePoint codePoint)
	{
		handleLivenessSet(livenessMap.get(codePoint));
		super.visit(codePoint);
	}
	
	public InterferenceGraph getInterferenceGraph()
	{
		return graph;
	}
}
