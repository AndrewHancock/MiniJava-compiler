package ir.ops;

import ir.visitor.IrVisitor;

public class Identifier implements Value
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
}
