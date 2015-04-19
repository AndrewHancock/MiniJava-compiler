package ir.regalloc;

public class StackOffset extends Value
{	
	private int stackOffset;
	
	public StackOffset(int stackOffset)
	{
		super(stackOffset);
	}

	public int getStackOffset()
	{
		return getValue();
	}
	
	@Override
	public String toString()
	{
		Integer offset = Integer.valueOf(stackOffset);
		return offset > 0 ? "-" + offset.toString() : offset.toString();
	}
}
