package ir.visitor;

import ir.Call;
import ir.BasicBlock;
import ir.BinOp;
import ir.CodePoint;
import ir.Frame;
import ir.Identifier;
import ir.SysCall;
import ir.Temporary;

public class DepthFirstIrVisitor implements IrVisitor
{
	@Override
	public void visit(Frame f)
	{
	}
	
	@Override
	public void visit(BasicBlock b)
	{
	}

	@Override
	public void visit(CodePoint c)
	{
	}

	@Override
	public void visit(BinOp b)
	{
	}

	@Override
	public void visit(Call c)
	{
	}

	@Override
	public void visit(SysCall s)
	{	
	}

	@Override
	public void visit(Temporary t)
	{	
	}

	@Override
	public void visit(Identifier i)
	{		
	}	

}
