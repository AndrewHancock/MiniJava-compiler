package ir.ops;

import ir.visitor.IrVisitor;

public class ConditionalJump implements Statement
{
	private RelationalOp condition;
	private Label label;
	
	public ConditionalJump(RelationalOp condition, Label label)
	{
		this.condition = condition;
		this.label = label;
	}

	public RelationalOp getCondition()
	{
		return condition;
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
	
	@Override
	public String toString()
	{
		return "if " + condition.toString() + " jump to " + label.getLabel();
	}
}
