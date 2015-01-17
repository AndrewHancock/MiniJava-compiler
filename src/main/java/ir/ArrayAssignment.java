package ir;

public class ArrayAssignment extends Assignment 
{

	private Value destIndex;
	public ArrayAssignment(Value src, Value dest, Value destIndex)
	{
		super(src, dest);
		this.destIndex = destIndex;
	}
	
	public Value getDestIndex()
	{
		return destIndex;
	}

}
