package ir.ops;

import ir.visitor.IrVisitor;

public class Label implements Statement
{
	private String label;
	
	public Label(String label)
	{
		this.label = label;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}
	
	public String getLabel()
	{
		return label;
	}
}
