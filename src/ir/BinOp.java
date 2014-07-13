package ir;

import ir.visitor.IrVisitor;

public class BinOp
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

	private Alias dest;
	private Alias src1;
	private Alias src2;
	
	public BinOp(Op op, Alias dest, Alias src1, Alias src2)
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
	
	public Alias getDest()
	{
		return dest;
	}
	
	public Alias getSrc1()
	{
		return src1;
	}
	
	public Alias getSrc2()
	{
		return src2;
	}
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
