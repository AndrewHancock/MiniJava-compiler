package ir.ops;

import ir.visitor.IrVisitor;

public class Assignment implements Statement
{
	private Expression src;
	private Expression dest;
	
	public Assignment(Expression src, Expression dest)
	{
		this.src = src;
		this.dest = dest;
	}


	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

	public Expression getSrc()
	{
		return src;
	}

	public Expression getDest()
	{
		return dest;
	}	
}
