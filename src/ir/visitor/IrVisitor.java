package ir.visitor;

import ir.Call;
import ir.BasicBlock;
import ir.BinOp;
import ir.CodePoint;
import ir.Frame;
import ir.Identifier;
import ir.SysCall;
import ir.Temporary;

public interface IrVisitor
{
	void visit(Frame f);
	void visit(BasicBlock b);
	void visit(CodePoint c);
	void visit(BinOp b);
	void visit(Call c);
	void visit(SysCall s);
	void visit(Temporary t);
	void visit(Identifier i);
}
