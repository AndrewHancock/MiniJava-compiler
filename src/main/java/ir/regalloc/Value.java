package ir.regalloc;

public abstract class Value
{	
	private int value;
	public Value(int value)
	{
		this.value = value;
	}
	
	protected int getValue()
	{
		return value;
	}
	
}
