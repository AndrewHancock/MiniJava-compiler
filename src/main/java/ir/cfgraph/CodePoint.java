package ir.cfgraph;

import ir.ops.Operation;
import ir.visitor.IrVisitor;

public class CodePoint
{	
	Operation op;
	
	public CodePoint(Operation op)
	{
		this.op = op;
	}
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	public Operation getOperation()
	{
		return op;
	}
}
