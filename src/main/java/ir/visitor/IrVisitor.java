package ir.visitor;

import ir.Temporary;
import ir.cfgraph.BasicBlock;
import ir.cfgraph.CodePoint;
import ir.cfgraph.Conditional;
import ir.cfgraph.Frame;
import ir.cfgraph.Loop;
import ir.ops.ArrayAccess;
import ir.ops.ArrayLength;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.Call;
import ir.ops.Identifier;
import ir.ops.IntegerLiteral;
import ir.ops.NewArray;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.SysCall;

public interface IrVisitor
{
	void visit(Frame f);
	void visit(BasicBlock b);	
	void visit(Conditional b);
	void visit(CodePoint c);
	void visit(BinOp b);
	void visit(Call c);
	void visit(Assignment assignment);
	void visit(SysCall s);
	void visit(Temporary t);
	void visit(Identifier i);	
	void visit(IntegerLiteral l);
	void visit(ArrayAccess a);	
	void visit(NewArray n);
	void visit(RecordDeclaration r);
	void visit(RecordAccess r);	
	void visit(RecordAllocation a);
	void visit(Return r);
	void visit(RelationalOp r);
	void visit(Loop l);
	void visit(ArrayLength a);
}
