package ir.backend;

public class StackOffset extends Value
{	
	private int stackOffset;
	
	public StackOffset(int stackOffset)
	{
		this.stackOffset = stackOffset;
	}

	public int getStackOffset()
	{
		return stackOffset;
	}
	
	@Override
	public String toString()
	{
		Integer offset = Integer.valueOf(stackOffset);
		return offset > 0 ? "-" + offset.toString() : offset.toString();
	}
}
