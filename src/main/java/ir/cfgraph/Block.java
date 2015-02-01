package ir.cfgraph;

import ir.ops.Statement;
import ir.visitor.IrVisitor;

public interface Block
{
	void accept(IrVisitor visitor);	
	void addParent(Block parent);
	void setSuccessor(Block successor);
	Block getSuccessor();	
	int getId();
	void addStatement(Statement statement);
	

}
