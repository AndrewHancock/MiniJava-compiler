package ir.ops;

import ir.visitor.IrVisitor;

import java.util.List;

public class SysCall extends Call
{

	public SysCall(String id, List<Expression> parameters)
	{
		super(id, parameters);
	}

	@Override
	public void  accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
