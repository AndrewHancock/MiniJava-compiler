package ir;

import ir.visitor.IrVisitor;

public class BinOp implements Operation
{
	public enum Op
	{
		ADD,
		SUBTRACT,
		MULT,
		AND,
		OR,
		NOT,
		EQ		
	}
	private Op op;

	private Value dest;
	private Value src1;
	private Value src2;
	
	public BinOp(Op op, Value dest, Value src1, Value src2)
	{
		this.op = op;
		this.dest = dest;
		this.src1 = src1;
		this.src2 = src2;
	}
	
	public Op getOp()
	{
		return op;
	}
	
	public Value getDest()
	{
		return dest;
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
