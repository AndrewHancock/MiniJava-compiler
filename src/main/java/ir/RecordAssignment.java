package ir;

import ir.visitor.IrVisitor;

public class RecordAssignment extends Assignment
{

	public RecordAssignment(Value src, Value dest)
	{
		super(src, dest);	
	}
	

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}

}
