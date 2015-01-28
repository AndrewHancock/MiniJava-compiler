package ir.ops;

public class ArrayAssignment extends Assignment 
{	
	private Expression destIndex;
	public ArrayAssignment(Expression src, Expression dest, Expression destIndex)
	{
		super(src, dest);
		this.destIndex = destIndex;
	}
	
	public Expression getDestIndex()
	{
		return destIndex;
	}

}
