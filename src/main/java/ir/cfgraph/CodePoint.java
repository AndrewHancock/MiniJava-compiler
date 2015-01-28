package ir.cfgraph;

import ir.ops.Statement;
import ir.visitor.IrVisitor;

public class CodePoint
{	
	Statement op;
	
	public CodePoint(Statement op)
	{
		this.op = op;
	}
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	public Statement getOperation()
	{
		return op;
	}
}
