package ir.ops;

import ir.visitor.IrVisitor;

import java.util.List;

public class SysCall extends Call
{

	public SysCall(String namespace, String id, List<Expression> parameters)
	{
		super(namespace, id, parameters);
	}

	@Override
	public void  accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
