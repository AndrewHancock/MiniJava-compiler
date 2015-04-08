package ir.cfgraph;

import java.util.HashSet;

public class TopDownVisitor implements Visitor
{
	private HashSet<CodePoint> visited = new HashSet<CodePoint>();
	@Override
	public void visit(BranchCodePoint codePoint)
	{
		visited.add(codePoint);
		if(!visited.contains(codePoint.getTakenSuccessor()))
			codePoint.getTakenSuccessor().accept(this);
		if(!visited.contains(codePoint.getNotTakenSuccessor()))
			codePoint.getNotTakenSuccessor().accept(this);		
	}

	@Override
	public void visit(LinearCodePoint codePoint)
	{
		visited.add(codePoint);
		if(!visited.contains(codePoint.getSuccessor()))
			codePoint.getSuccessor().accept(this);		
	}
}
