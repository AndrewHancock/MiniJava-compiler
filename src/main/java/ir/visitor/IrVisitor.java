package ir.visitor;

import ir.ArrayAccess;
import ir.ArrayAssignment;
import ir.Assignment;
import ir.Call;
import ir.BasicBlock;
import ir.BinOp;
import ir.CodePoint;
import ir.ConditionalBasicBlock;
import ir.Frame;
import ir.Identifier;
import ir.IntegerLiteral;
import ir.NewArray;
import ir.RecordAccess;
import ir.RecordAssignment;
import ir.RecordDeclaration;
import ir.SysCall;
import ir.Temporary;

public interface IrVisitor
{
	void visit(Frame f);
	void visit(BasicBlock b);
	void visit(ConditionalBasicBlock b);
	void visit(CodePoint c);
	void visit(BinOp b);
	void visit(Call c);
	void visit(Assignment assignment);
	void visit(SysCall s);
	void visit(Temporary t);
	void visit(Identifier i);
	void visit(IntegerLiteral l);
	void visit(ArrayAccess a);
	void visit(ArrayAssignment a);
	void visit(NewArray n);
	void visit(RecordDeclaration r);
	void visit(RecordAccess r);
	void visit(RecordAssignment r);
}
