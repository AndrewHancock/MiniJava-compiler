package ir.ops;

import ir.visitor.IrVisitor;

public class RelationalOp implements Expression
{
	public enum Op
	{
		LTE,
		LT,
		EQ
	}
	private Op op;	
	private Expression src1;
	private Expression src2;
	
	public RelationalOp(Op op, Expression src1, Expression src2)
	{
		this.op = op;		
		this.src1 = src1;
		this.src2 = src2;
	}
	
	public Op getOp()
	{
		return op;
	}
	
	public Expression getSrc1()
	{
		return src1;
	}
	
	public Expression getSrc2()
	{
		return src2;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

}
