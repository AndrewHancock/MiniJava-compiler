package ir.ops;

import ir.visitor.IrVisitor;

public class Assignment implements Operation
{
	private Value src;
	private Value dest;
	
	public Assignment(Value src, Value dest)
	{
		this.src = src;
		this.dest = dest;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

	public Value getSrc()
	{
		return src;
	}

	public Value getDest()
	{
		return dest;
	}	
}
