package ir.ops;

import ir.visitor.IrVisitor;

public class Return implements Statement
{
	private Expression operand;
	public Return(Expression operand)
	{
		this.operand = operand;
	}
	
	public Expression getSource()
	{
		return operand;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
		
	}
}
