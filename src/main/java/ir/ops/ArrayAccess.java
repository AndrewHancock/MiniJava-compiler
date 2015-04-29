package ir.ops;

import ir.visitor.IrVisitor;

public class ArrayAccess implements Expression
{
	private Expression reference;	
	private DataType type;
	private Expression index;
	
	
	public ArrayAccess(Expression reference, DataType type, Expression index)
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
	
	public Expression getReference()
	{
		return reference;
	}

	public DataType getType()
	{
		return type;
	}

	public Expression getIndex()
	{
		return index;
	}
	
	@Override
	public String toString()
	{
		return reference.toString() + "[" + index.toString() + "]";
	}
}
