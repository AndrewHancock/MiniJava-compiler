package ir.ops;

import ir.visitor.IrVisitor;

public interface Value
{	
	void accept(IrVisitor visitor);	
}
