package ir.cfgraph;

import ir.visitor.IrVisitor;

public interface Block
{
	void accept(IrVisitor visitor);
	Block getSuccessor();

}
