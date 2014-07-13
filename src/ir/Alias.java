package ir;

import ir.visitor.IrVisitor;

public interface Alias
{
	String getName();	
	void accept(IrVisitor visitor);
}
