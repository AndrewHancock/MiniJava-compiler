package ir.ops;

import ir.visitor.IrVisitor;

public class RecordAssignment extends Assignment
{

	public RecordAssignment(Expression src, Expression dest)
	{
		super(src, dest);	
	}
	

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}

}
