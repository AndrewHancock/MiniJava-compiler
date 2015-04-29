package ir.ops;

import ir.visitor.IrVisitor;

public class ArrayLength implements Expression
{
	private Expression id;
	
	public ArrayLength(Expression id)
	{
		this.id = id;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	public Expression getExpression()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return id + ".length";
	}
}
