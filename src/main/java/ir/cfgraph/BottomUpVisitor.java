package ir.cfgraph;


import ir.ops.Jump;

import java.util.HashSet;

public abstract class BottomUpVisitor implements Visitor
{
	private HashSet<CodePoint> visited = new HashSet<CodePoint>();
	public void clear()
	{
		visited.clear();
	}
	
	private void handleCodepoint(CodePoint codePoint)
	{		
		for(CodePoint parent : codePoint.getParents())
		{
			if(!visited.contains(parent))
			{
				beforeParent(codePoint);
				parent.accept(this);
			}
		}
	}
	
	protected void beforeParent(CodePoint codePoint)
	{
		
	}
	
	public void visit(BranchCodePoint codePoint)
	{
		handleCodepoint(codePoint);
	}
	
	public void visit(LinearCodePoint codePoint)
	{
		if(codePoint.getStatement() instanceof Jump)
			visited.add(codePoint);
		handleCodepoint(codePoint);
	}
}
