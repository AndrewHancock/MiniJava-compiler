package ir;

import ir.visitor.IrVisitor;

public interface Operation
{
	public void accept(IrVisitor visitor);
}
