package ir;

import ir.visitor.IrVisitor;

public class ArrayAccess implements Value
{
	private Value reference;	
	private DataType type;
	private Value index;
	
	
	public ArrayAccess(Value reference, DataType type, Value index)
	{
		this.reference = reference;
		this.type = type;
		this.index = index;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}
	
	public Value getReference()
	{
		return reference;
	}

	public DataType getType()
	{
		return type;
	}

	public Value getIndex()
	{
		return index;
	}
}
