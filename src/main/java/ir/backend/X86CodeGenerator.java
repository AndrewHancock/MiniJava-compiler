package ir.backend;

import ir.ops.ArrayAccess;
import ir.ops.ArrayAllocation;
import ir.ops.ArrayLength;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.Call;
import ir.ops.ConditionalJump;
import ir.ops.Expression;
import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;
import ir.ops.Jump;
import ir.ops.Label;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.Statement;
import ir.ops.SysCall;
import ir.regalloc.GraphColorAllocator;
import ir.regalloc.RegisterAllocator;
import ir.regalloc.Value;
import ir.visitor.IrVisitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

public class X86CodeGenerator implements IrVisitor
{
	private PrintStream out;
	private HashMap<String, RecordDeclaration> recordMap = new HashMap<String, RecordDeclaration>();

	private WinX64RegisterManager registers = new WinX64RegisterManager();
	private final String[] reservedRegisters = { "rax", "rbx" };

	public X86CodeGenerator(PrintStream out)
	{
		this.out = out;
	}

	private String valueString(String id)
	{
		return registers.valueString(id);
	}

	private String valueString(Value v)
	{
		return registers.valueString(v);
	}

	private String idString(Value value)
	{
		return registers.idString(value);
	}

	private void emit(String text)
	{
		out.println("\t" + text);
	}

	private void emit(String text, String comment)
	{
		out.println("\t" + text + "\t" + "# " + comment);
	}

	private void emitLabel(String text)
	{
		out.println(text + ":");
	}

	private void emitComment(String text)
	{
		out.println("\t" + "#" + text);
	}

	private void initFile(String startFrameId)
	{
		emitComment("General constants used for output");
		emitLabel("print_num");
		emit(".ascii \"%d \\0\"");
		emitLabel("newline");
		emit(".ascii \"\\n\\0\"");
		emit(".def main");

		emit(".globl main");
		emitLabel("main");
		emitComment("Prologue to main");
		emit("pushq %rbp");
		emit("movq %rsp, %rbp");
		emit("call __main   #Call c library main");
		emit("call " + startFrameId + "  #call the starting frame");
		emit("leave");
		emit("ret");
	}

	private boolean startFrame;
	private RegisterAllocator allocator = new GraphColorAllocator();

	private void emitFunctionComment(FunctionDeclaration f)
	{
		emitComment("Function: " + f.getNamespace() + "." + f.getId());
		emitComment("");
		emitComment("Register and Stack Allocation:");

		for (Identifier id : f.getParams())
		{
			if (registers.value(id.getId()) != null)
				emitComment(valueString(id.getId()) + " - " + id.getId());
		}

		for (Identifier id : f.getLocals())
		{
			if (registers.value(id.getId()) != null)
				emitComment(valueString(id.getId()) + " - " + id.getId());
			else
				emitComment("Unused - " + id.getId());
		}
		for (Identifier id : f.getTemporaries())
		{
			if (registers.value(id.getId()) != null)
				emitComment(valueString(id.getId()) + " - " + id.getId());
			else
				emitComment("Unused - " + id.getId());
		}

	}

	public void emitPrologue(FunctionDeclaration f)
	{
		out.println("");
		emitComment("Begin prologue");
	}

	@Override
	public void visit(FunctionDeclaration f)
	{
		if (!startFrame)
		{
			startFrame = true;
			initFile(f.getNamespace() + "_" + f.getId());
		}
		out.println();
		registers.init(f);
		emitFunctionComment(f);

		String nameNames = f.getNamespace() + "_";
		emitLabel(nameNames + f.getId());
		emit("pushq %rbp");
		emit("movq %rsp, %rbp");

		if (allocator.getStackSize() > 0)
			emit("subl $" + allocator.getStackSize() + " , %rsp",
					"Reserve spsace for locals and temporaries.");

		emitPrologue(f);

		for (Statement statement : f.getStatements())
			statement.accept(this);

		emit("leave");
		emit("ret");
		out.println();
	}

	private Value dest;

	@Override
	public void visit(Assignment assignment)
	{
		assignment.getDest().accept(this);
		dest = currentValue;
		assignment.getSrc().accept(this);
		dest = null;
	}

	private void assignCallParameters(List<Expression> params, int startingRegister)
	{
		int paramCount = 0;
		for (paramCount = 0; paramCount < params.size() - 1
				&& paramCount < registers.numCallParams() - startingRegister; paramCount++)
		{
			params.get(paramCount).accept(this);
			emit("movq " + valueString(currentValue) + ", %"
					+ registers.getParamReg(startingRegister + paramCount),
					"Assign parameteter " + idString(currentValue)
							+ " to param register "
							+ (startingRegister + paramCount));
		}

		for (; paramCount < params.size() - 1; paramCount++)
		{
			params.get(paramCount).accept(this);
			emit("pushq " + valueString(currentValue), "Save parameter "
					+ idString(currentValue) + " to the stack");

		}
	}

	private void restoreParameters(List<Expression> params, int startingRegister)
	{

		if (params.size() > registers.numCallParams())
		{
			int callStackUsage = (params.size() - registers.numCallParams())
					* registers.wordSize();
			emit("addq $" + callStackUsage + ", %rsp", "Clean up parameter stack");

		}
		int paramCount = 0;
		for (paramCount = 0; paramCount < params.size() - 1
				&& paramCount < registers.numCallParams() - startingRegister; paramCount++)
		{
			params.get(paramCount).accept(this);
			emit("movq " + valueString(currentValue) + ", %"
					+ registers.getParamReg(paramCount + startingRegister),
					"Assign parameteter " + idString(currentValue)
							+ " to param register "
							+ (paramCount + startingRegister));
		}

	}

	@Override
	public void visit(Call call)
	{
		emitComment("Begin call " + call.getId());
		assignCallParameters(call.getParameters(), 0);
		emit("call " + call.getNamespace() + "_" + call.getId());
		restoreParameters(call.getParameters(), 0);
		if (dest != null)
			emit("movq %rax, " + valueString(dest), "Assign return value to "
					+ idString(dest));
		emitComment("End call " + call.getId());
	}

	@Override
	public void visit(SysCall call)
	{
		emitComment("Begin " + call.getId());

		emit("movq $print_num, %" + registers.getParamReg(0),
				"Move address of string '%d ' to param register 1");

		if (call.getId().equals("print") || call.getId().equals("println"))
		{
			assignCallParameters(call.getParameters(), 1);
			emit("call printf");
		}

		if (call.getId().equals("println"))
		{
			emitComment("Print new line");
			emit("pushq $newline");
			emit("movq $newline, %" + registers.getParamReg(0),
					"Move address of string '%d ' to param register 1");
			emit("call printf");
		}
		restoreParameters(call.getParameters(), 1);
		emitComment("End " + call.getId());
	}

	private Value currentValue;

	@Override
	public void visit(Identifier i)
	{
		currentValue = registers.value(i.getId());
	}

	@Override
	public void visit(ir.ops.IntegerLiteral l)
	{
		currentValue = new IntegerLiteral(l.getValue());
	}

	private String getOpOpcode(BinOp.Op op)
	{
		switch (op)
		{
		case ADD:
			return "addq";
		case MULT:
			return "imulq";
		case SUBTRACT:
			return "subq";
		default:
			return null;
		}
	}

	public void visit(BinOp b)
	{
		BinOp.Op op = b.getOp();
		if (op != null)
		{
			b.getSrc1().accept(this);
			Value left = currentValue;
			b.getSrc2().accept(this);
			Value right = currentValue;

			emit("movq " + valueString(left) + " , " + valueString(dest), "Moving "
					+ valueString(left) + " to " + valueString(dest));

			emit(getOpOpcode(b.getOp()) + " " + valueString(right) + ", "
					+ valueString(dest), "BinOp on " + idString(left) + " and "
					+ idString(right));
		}
	}

	@Override
	public void visit(ArrayAccess a)
	{
		emitComment("Access an array element");
		a.getReference().accept(this);
		emit("movq " + valueString(currentValue) + ", %rax", "Load array reference");
		a.getIndex().accept(this);
		emit("movq " + valueString(currentValue) + ", %rbx", "Load array index");
		emit("leaq 0(,%rbx,4), %rbx", "Compute offset and store to %rbx");
		emit("addq %rbx, %rax");
		if (dest != null)
			emit("movq (%eax), " + valueString(dest), "Assign array element to "
					+ idString(dest));
		else
			currentValue = new RegisterDereference(0);
	}

	@Override
	public void visit(ArrayAllocation n)
	{
		out.println();
		emitComment("Allocate new array");
		n.getSize().accept(this);
		emit("movq " + valueString(currentValue) + ", %" + registers.getParamReg(0),
				"Load size from " + idString(currentValue) + " to param register 1");
		emit("movq %" + registers.getParamReg(0) + ", %" + reservedRegisters[0],
				"Save size before malloc call to reserved register");
		emit("imulq $" + registers.wordSize() + ", %" + registers.getParamReg(0),
				"Multiply number of elements by WORD_SIZE");
		emit("addq $" + registers.wordSize() + ", %" + registers.getParamReg(0),
				"Request WORD_SIZE additional bytes to store size");
		emit("call malloc");
		emit("movq %" + reservedRegisters[0] + ", (%rax)",
				"Store array length at zeroth location");
		if (dest != null)
			emit("movq %rax, " + valueString(dest), "Assign new array to "
					+ idString(dest));
		emitComment("End array allocation");
		out.println();
	}

	@Override
	public void visit(RecordDeclaration r)
	{
		recordMap.put(r.getNamespace() + r.getId(), r);
	}

	@Override
	public void visit(RecordAccess r)
	{
		int offset = (r.getFieldIndex() * registers.wordSize());

		out.println();
		emitComment("Record Acccess");
		r.getIdentifier().accept(this);
		emit("movq " + valueString(currentValue) + ", %" + reservedRegisters[0],
				"Move address of record to reserved register 1");
		if (offset > 0)
			emit("addq $" + offset + ", %" + reservedRegisters[0],
					"Add field offset to record address");
		currentValue = new RegisterDereference(0);
		emitComment("End record access");
		out.println();
	}

	@Override
	public void visit(RecordAllocation a)
	{
		RecordDeclaration decl = recordMap.get(a.getNamespace() + a.getTypeId());
		if (decl.getFieldCount() > 0)
		{
			emitComment("Record allocation");
			emit("movq $" + (decl.getFieldCount() * registers.wordSize()) + ", "
					+ "%" + registers.getParamReg(0));
			emit("call malloc");
			if (dest != null)
				emit("movq %rax, " + valueString(dest),
						"Assign address of record to " + idString(dest));
			emitComment("End record allocation");
		}
	}

	@Override
	public void visit(Return r)
	{
		r.getSource().accept(this);
		emit("movq " + valueString(currentValue) + ", %rax", "Move "
				+ idString(currentValue) + " to result register");
	}

	private String getJumpInstruction(RelationalOp.Op op)
	{
		switch (op)
		{
		case LTE:
			return "jle";
		case LT:
			return "jl";
		case EQ:
			return "je";
		default:
			throw new RuntimeException("Unrecognized relational operation.");
		}
	}

	int relationalCount;

	@Override
	public void visit(RelationalOp r)
	{
		int relationalCount = this.relationalCount++;
		r.getSrc1().accept(this);
		Value leftOperand = currentValue;
		r.getSrc2().accept(this);
		Value rightOperand = currentValue;
		emit("cmp " + valueString(rightOperand) + ", " + valueString(leftOperand),
				"Compare operand " + idString(leftOperand) + " and "
						+ idString(rightOperand));
		emit(getJumpInstruction(r.getOp()) + " relational_true_" + relationalCount);
		emitLabel("relational_false_" + relationalCount);
		if (dest != null)
			emit("movq $0, " + valueString(dest), "Assigning 'false' to "
					+ idString(dest));
		else
			emit("movq $0, %" + reservedRegisters[0],
					"Moving 'false' to reserved register 1.");
		emit("jmp relational_end_" + relationalCount);
		emitLabel("relational_true_" + relationalCount);
		if (dest != null)
			emit("movq $1, " + valueString(dest), "Assigning 'true' to "
					+ idString(dest));
		else
			emit("movq $1, %" + reservedRegisters[0],
					"Moving 'true' to reserved register 1.");
		emitLabel("relational_end_" + relationalCount);
	}

	@Override
	public void visit(ArrayLength a)
	{
		a.getExpression().accept(this);
		currentValue = new RegisterDereference(0);
	}

	@Override
	public void visit(Label label)
	{
		emitLabel(label.getLabel());
	}

	@Override
	public void visit(ConditionalJump j)
	{
		j.getCondition().accept(this);
		if (currentValue instanceof IntegerLiteral)
		{

		}
		else
		{
			emit("cmp $1, " + valueString(currentValue), "Compare 'true' to "
					+ idString(currentValue));
			emit("je " + j.getLabel().getLabel());
		}
	}

	@Override
	public void visit(Jump j)
	{
		emit("jmp " + j.getLabel().getLabel(), "Uncondintional jump to "
				+ j.getLabel().toString());
	}
}
