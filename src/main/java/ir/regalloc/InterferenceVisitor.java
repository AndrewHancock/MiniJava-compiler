package ir.regalloc;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.CodePoint;
import ir.cfgraph.LinearCodePoint;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

public class InterferenceVisitor extends BottomUpVisitor
{
	private class Node
	{
		HashSet<Node> neighbors = new HashSet<Node>();
	}

	private Node[] nodes;
	
	public void clear()
	{
		nodes = null;
	}
	
	private void updateGraph(CodePoint codePoint)
	{
		BitSet liveness = codePoint.getLiveness();
		if(nodes == null)
			nodes = new Node[liveness.size()];

		for(int i = 0; i < liveness.size(); i++)
		{
			if(liveness.get(i))
			{
				Node node = nodes[i];
				if(node == null)					
					node = nodes[i] = new Node();
				
			}
			
		}

	}
	@Override
	public void visit(BranchCodePoint codePoint)
	{
		super.visit(codePoint);
	}

	@Override
	public void visit(LinearCodePoint codePoint)
	{
		super.visit(codePoint);

	}

}
