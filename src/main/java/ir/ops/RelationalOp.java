package ir.ops;

import ir.visitor.IrVisitor;

public class RelationalOp implements Value
{
	public enum Op
	{
		LTE
	}
	private Op op;	
	private Value src1;
	private Value src2;
	
	public RelationalOp(Op op, Value src1, Value src2)
	{
		this.op = op;		
		this.src1 = src1;
		this.src2 = src2;
	}
	
	public Op getOp()
	{
		return op;
	}
	
	public Value getSrc1()
	{
		return src1;
	}
	
	public Value getSrc2()
	{
		return src2;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

}
