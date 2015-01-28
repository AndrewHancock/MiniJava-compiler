package ir.ops;

import ir.visitor.IrVisitor;

public interface Statement
{
	public void accept(IrVisitor visitor);
}
