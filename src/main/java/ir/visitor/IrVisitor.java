package ir.visitor;

import ir.Temporary;
import ir.ops.ArrayAccess;
import ir.ops.ArrayAllocation;
import ir.ops.ArrayLength;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.Call;
import ir.ops.ConditionalJump;
import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;
import ir.ops.IntegerLiteral;
import ir.ops.Jump;
import ir.ops.Label;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.SysCall;

public interface IrVisitor
{
	void visit(ArrayAccess a);	
	void visit(ArrayAllocation n);
	void visit(ArrayLength a);
	void visit(Assignment assignment);
	void visit(BinOp b);
	void visit(Call c);
	void visit(ConditionalJump j);
	void visit(FunctionDeclaration f);	
	void visit(Identifier i);	
	void visit(IntegerLiteral l);
	void visit(Jump j);
	void visit(Label l);
	void visit(RecordAccess r);	
	void visit(RecordAllocation a);
	void visit(RecordDeclaration r);
	void visit(RelationalOp r);
	void visit(Return r);	
	void visit(SysCall s);	
	void visit(Temporary t);	
}
