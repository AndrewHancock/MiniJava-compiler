package ir.ops;

import ir.visitor.IrVisitor;

import java.util.List;

public class SysCall extends Call
{

	public SysCall(String id, List<Value> parameters, Value dest)
	{
		super(id, parameters, dest);
	}

	@Override
	public void  accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
