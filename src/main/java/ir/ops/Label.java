package ir.ops;

import ir.visitor.IrVisitor;

public enum Label implements Statement
{
	TRUE, FALSE, END, TEST, BODY;

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}
}
