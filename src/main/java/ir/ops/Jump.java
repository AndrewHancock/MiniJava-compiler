package ir.ops;

import ir.visitor.IrVisitor;

public class Jump implements Statement
{
	private Label label;
	
	public Jump(Label label)
	{
		this.label = label;
	}
	
	public Label getLabel()
	{
		return label;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}
}
