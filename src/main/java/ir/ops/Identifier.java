package ir.ops;

import ir.visitor.IrVisitor;

public class Identifier implements Expression
{
	private String id;
	public Identifier(String id) { this.id = id; }	
	
	public String getId()
	{
		return id;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	@Override
	public String toString()
	{
		return id;
	}
}
