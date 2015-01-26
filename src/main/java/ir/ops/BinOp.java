package ir.ops;

import ir.visitor.IrVisitor;

public class BinOp implements Value 
{
	public enum Op
	{
		ADD,
		SUBTRACT,
		MULT,
		AND,
		OR
	}
	private Op op;	
	private Value src1;
	private Value src2;
	
	public BinOp(Op op, Value src1, Value src2)
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
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
