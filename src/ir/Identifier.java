package ir;

import ir.visitor.IrVisitor;

public class Identifier implements Alias
{
	private String id;
	public Identifier(String id) { this.id = id; }
	
	@Override
	public String getName()
	{
		return id;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
