package ir;

import ir.visitor.IrVisitor;

public class NewArray implements Value
{
	private DataType type;
	private Value size;
	
	public NewArray(DataType type, Value size)
	{
		this.type = type;
		this.size = size;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	public Value getSize()
	{
		return size;
	}

}
