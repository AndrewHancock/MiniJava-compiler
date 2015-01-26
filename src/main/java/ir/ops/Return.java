package ir.ops;

import syntaxtree.Identifier;
import ir.visitor.IrVisitor;

public class Return implements Operation
{
	private Value operand;
	public Return(Value operand)
	{
		this.operand = operand;
	}
	
	public Value getSource()
	{
		return operand;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
		
	}

}
