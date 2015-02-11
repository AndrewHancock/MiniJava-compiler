package ir.ops;

import ir.visitor.IrVisitor;

public class ArrayLength implements Expression
{
	private Identifier id;
	
	public ArrayLength(Identifier id)
	{
		this.id = id;
	}
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	public Identifier getExpression()
	{
		return id;
	}
}
