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

	private void emitMove(String src, String dest, String comment)
	{
		emit("movq " + src + ", " + dest, comment);
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
		for (int i = 0; i < registers.getAllocator().getNumRegistersUsed()
				- registers.getCallerSavedCount(); i++)
		{
			emit("push %" + registers.getCalleeSavedReg(i),
					"Save callee save register " + i);
		}
	}

	public void emitEpilogue(FunctionDeclaration f)
	{		
		for (int i = registers.getAllocator().getNumRegistersUsed()
				- registers.getCallerSavedCount() - 1; i >= 0; i--)
		{
			emit("pop %" + registers.getCalleeSavedReg(i),
					"Restore callee save register " + i);
		}
	}

	private FunctionDeclaration currentFunc;

	@Override
	public void visit(FunctionDeclaration f)
	{
		currentFunc = f;
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

		if (registers.getAllocator().getStackSize() > 0)
			emit("subq $" + registers.getAllocator().getStackSize() + " , %rsp",
					"Reserve spsace for locals and temporaries.");

		emitPrologue(f);

		for (Statement statement : f.getStatements())
			statement.accept(this);

		emitEpilogue(f);
		emit("leave");
		emit("ret");
		out.println();
	}

	private Value dest;
	private Value src;

	@Override
	public void visit(Assignment assignment)
	{
		emitComment(assignment.toString());
		if (assignment.getDest() instanceof RecordAccess || assignment.getDest() instanceof ArrayAccess)
		{
			assignment.getSrc().accept(this);
			src = currentValue;
			assignment.getDest().accept(this);
			src = null;
		}
		else
		{
			assignment.getDest().accept(this);
			dest = currentValue;
			assignment.getSrc().accept(this);
			dest = null;
		}

	}

	private void saveCallerSaveRegisters()
	{
		for (int i = 0; i < registers.numCallParams()
				&& i < currentFunc.getParams().size(); i++)
		{
			emit("pushq %" + registers.getParamReg(i), "Save param register " + i);
		}
		for (int i = 0; i < registers.getCallerSavedCount(); i++)
		{
			emit("pushq %" + registers.getCallerSavedReg(i),
					"Save caller save register " + i);
		}
		emit("subq $32, %rsp", "Create shadow space on the stack");
	}

	private void restoreCallerSaveRegisters()
	{
		emit("addq $32, %rsp", "Clean up shadow stack space");		
		for (int i = registers.getCallerSavedCount() - 1; i >= 0; i--)
		{
			emit("popq %" + registers.getCallerSavedReg(i),
					"Restore caller save register " + i);
		}
		int lastParamRegister;
		if (currentFunc.getParams().size() >= registers.numCallParams())
			lastParamRegister = registers.numCallParams() - 1;
		else
			lastParamRegister = currentFunc.getParams().size() - 1;

		for (int i = lastParamRegister; i >= 0; i--)
		{
			emit("popq %" + registers.getParamReg(i), "Restore param register " + i);
		}
	}

	private void assignCallParameters(List<Expression> params, int startingRegister)
	{

		int paramCount = 0;
		for (paramCount = 0; paramCount < params.size()
				&& paramCount < registers.numCallParams() - startingRegister; paramCount++)
		{
			params.get(paramCount).accept(this);
			emit("movq " + valueString(currentValue) + ", %"
					+ registers.getParamReg(startingRegister + paramCount),
					"Assign " + idString(currentValue) + " to param register "
							+ (startingRegister + paramCount));
		}

		for (; paramCount < params.size(); paramCount++)
		{
			params.get(paramCount).accept(this);
			emit("pushq " + valueString(currentValue), "Save parameter "
					+ idString(currentValue) + " to the stack");
		}
	}

	@Override
	public void visit(Call call)
	{
		emitComment("Begin call " + call.getId());
		saveCallerSaveRegisters();
		Value assignTarget = dest;
		dest = null;
		assignCallParameters(call.getParameters(), 0);
		emit("call " + call.getNamespace() + "_" + call.getId());
		if (call.getParameters().size() > registers.numCallParams())
		{
			int callStackUsage = (call.getParameters().size() - registers
					.numCallParams()) * registers.wordSize();
			emit("addq $" + callStackUsage + ", %rsp", "Clean up parameter stack");

		}
		restoreCallerSaveRegisters();
		if (assignTarget != null)
			emit("movq %rax, " + valueString(assignTarget),
					"Assign return value to " + idString(assignTarget));
		emitComment("End call " + call.getId());
	}

	@Override
	public void visit(SysCall call)
	{
		saveCallerSaveRegisters();
		emit("movq $print_num, %" + registers.getParamReg(0),
				"Move address of string '%d ' to param register 0");

		assignCallParameters(call.getParameters(), 1);
		if (call.getId().equals("print") || call.getId().equals("println"))
		{
			emit("call printf");
		}

		if (call.getId().equals("println"))
		{			
			emit("movq $newline, %" + registers.getParamReg(0),
					"Move address of string '\\n\\0' to param register 0");
			emit("call printf", "Print new line");
		}
		restoreCallerSaveRegisters();
	}

	private Value currentValue;

	@Override
	public void visit(Identifier i)
	{
		if (dest != null)
		{
			emit("movq " + valueString(i.getId()) + ", " + valueString(currentValue),
					"Assign " + i.getId() + " to " + idString(currentValue));
			dest = null;
		}
		currentValue = registers.value(i.getId());
	}

	@Override
	public void visit(ir.ops.IntegerLiteral l)
	{
		if (dest != null)
		{
			emit("movq $" + l.getValue() + ", " + valueString(currentValue),
					"Assign literal to " + idString(currentValue));
			dest = null;
		}
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
		Value assignTarget = dest;
		dest = null;

		BinOp.Op op = b.getOp();

		if (op != null)
		{
			b.getSrc1().accept(this);
			Value left = currentValue;
			b.getSrc2().accept(this);
			Value right = currentValue;

			if (assignTarget != null)
			{
				emit("movq " + valueString(left) + " , " + valueString(assignTarget),
						"Moving " + valueString(left) + " to "
								+ valueString(assignTarget));
			}

			emit(getOpOpcode(b.getOp()) + " " + valueString(right) + ", "
					+ valueString(assignTarget), "BinOp on " + idString(left)
					+ " and " + idString(right));
		}
	}

	@Override
	public void visit(ArrayAccess a)
	{
		emitComment("Access an array element");
		Value assignTarget = dest;
		dest = null;
		a.getIndex().accept(this);
		emit("movq " + valueString(currentValue) + ", %" + reservedRegisters[0], "Load array index to reserved register");
		emit("imulq $" + registers.wordSize() + ", %" + reservedRegisters[0], "Multiply array index by WORD_SIZE");
		emit("addq $" + registers.wordSize() + ", %" + reservedRegisters[0] , "Add WORD_SIZE to pointer to account for size word");
		a.getReference().accept(this);
		emit("addq " + valueString(currentValue) + ", %rax", "Add array address to offset");
		
		if (assignTarget != null)
			emit("movq (%eax), " + valueString(assignTarget),
					"Assign array element to " + idString(assignTarget));
		else
			emit("movq " + valueString(src) + ", (%rax)", "Assign " + idString(src) + " to array element.");
	}

	@Override
	public void visit(ArrayAllocation n)
	{
		out.println();
		emitComment("Allocate new array");
		Value assignTarget = this.dest;
		this.dest = null;
		n.getSize().accept(this);
		saveCallerSaveRegisters();
		emit("movq " + valueString(currentValue) + ", %" + registers.getParamReg(0),
				"Load size from " + idString(currentValue) + " to param register 1");
		emit("imulq $" + registers.wordSize() + ", %" + registers.getParamReg(0),
				"Multiply number of elements by WORD_SIZE");
		emit("addq $" + registers.wordSize() + ", %" + registers.getParamReg(0),
				"Request WORD_SIZE additional bytes to store size");
		emit("call malloc");
		restoreCallerSaveRegisters();
		emit("movq " + valueString(currentValue) + ", (%rax)",
				"Store array length at zeroth location");
		if (assignTarget != null)
			emit("movq %rax, " + valueString(assignTarget), "Assign new array to "
					+ idString(assignTarget));

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
		Value assignTarget = dest;
		dest = null;
		r.getIdentifier().accept(this);
		emit("movq " + valueString(currentValue) + ", %" + reservedRegisters[0],
				"Move address of record to reserved register 1");
		if (offset > 0)
			emit("addq $" + offset + ", %" + reservedRegisters[0],
					"Add field offset to record address");

		if (assignTarget != null)
			emitMove("(%rax)", valueString(assignTarget), " Assign field to "
					+ idString(assignTarget));
		else
			emitMove(valueString(src), "(%rax)", " Assign " + idString(src)
					+ " to field.");

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
			saveCallerSaveRegisters();
			emit("movq $" + (decl.getFieldCount() * registers.wordSize()) + ", "
					+ "%" + registers.getParamReg(0));
			emit("call malloc");
			restoreCallerSaveRegisters();
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
		Value assignTarget = dest;
		dest = null;
		r.getSrc1().accept(this);
		Value leftOperand = currentValue;
		r.getSrc2().accept(this);
		Value rightOperand = currentValue;
		emit("cmp " + valueString(rightOperand) + ", " + valueString(leftOperand),
				"Compare operand " + idString(leftOperand) + " and "
						+ idString(rightOperand));
		emit(getJumpInstruction(r.getOp()) + " relational_true_" + relationalCount);
		emitLabel("relational_false_" + relationalCount);
		if (assignTarget != null)
			emit("movq $0, " + valueString(assignTarget), "Assigning 'false' to "
					+ idString(assignTarget));
		else
			emit("movq $0, %" + reservedRegisters[0],
					"Moving 'false' to reserved register 1.");
		emit("jmp relational_end_" + relationalCount);
		emitLabel("relational_true_" + relationalCount);
		if (assignTarget != null)
			emit("movq $1, " + valueString(assignTarget), "Assigning 'true' to "
					+ idString(assignTarget));
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
		emitComment(j.toString());
		j.getCondition().accept(this);

		emit("cmp $1, %" + reservedRegisters[0],
				"Compare 'true' to reserved register");
		emit("je " + j.getLabel().getLabel());

	}

	@Override
	public void visit(Jump j)
	{
		emit("jmp " + j.getLabel().getLabel(), "Uncondintional jump to "
				+ j.getLabel().toString());
	}
}
