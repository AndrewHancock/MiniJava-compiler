package ir.cfgraph;


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
		boolean first = true;
		for(CodePoint parent : codePoint.getParents())
		{
			parent.accept(this);
			
			if (first)
			{
				first = false;
			}
			else
			{
				joinCallback(codePoint);
			}
		}
	}
	
	protected void joinCallback(CodePoint codePoint)
	{
		
	}
	
	public void visit(BranchCodePoint codePoint)
	{
		visited.add(codePoint);
		handleCodepoint(codePoint);
	}
	
	public void visit(LinearCodePoint codePoint)
	{
		visited.add(codePoint);
		handleCodepoint(codePoint);
	}
}
