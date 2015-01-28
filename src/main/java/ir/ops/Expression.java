package ir.ops;

import ir.visitor.IrVisitor;

public interface Expression
{	
	void accept(IrVisitor visitor);	
}
