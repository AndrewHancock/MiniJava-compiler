package ir.ops;

import ir.visitor.IrVisitor;

public class IdentifierExp implements Value
{
	private String id;
	public IdentifierExp(String id) { this.id = id; }	
	
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
