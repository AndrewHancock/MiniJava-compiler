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
import ir.visitor.IrVisitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

public class X86CodeGenerator implements IrVisitor
{
	private PrintStream out;
	private HashMap<String, RecordDeclaration> recordMap = new HashMap<String, RecordDeclaration>();

	private WinX64RegisterManager registers = new WinX64RegisterManager();

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
		return v.toString();
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
		emitComment("Registers allocated: " + registers.getAllocatedRegisterCount());
		emitComment("Spills: " + registers.getSpillCount());

		emitComment("Input Parameters:");
		for (Identifier id : f.getParams())
		{
			if (registers.value(id.getId()) != null)
				emitComment(valueString(id.getId()) + " - " + id.getId());
		}

		emitComment("Locals:");
		for (Identifier id : f.getLocals())
		{
			if (registers.value(id.getId()) != null)
				emitComment(valueString(id.getId()) + " - " + id.getId());
			else
				emitComment("Unused - " + id.getId());
		}
		
		emitComment("Temporaries:");
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
			emit("pushq " + registers.getCalleeSavedReg(i),
					"Save callee save register " + i);
		}
	}

	public void emitEpilogue(FunctionDeclaration f)
	{
		for (int i = registers.getAllocator().getNumRegistersUsed()
				- registers.getCallerSavedCount() - 1; i >= 0; i--)
		{
			emit("popq " + registers.getCalleeSavedReg(i),
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
		if (assignment.getDest() instanceof RecordAccess
				|| assignment.getDest() instanceof ArrayAccess)
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
		for (int i = 0; i < registers.getCallerSavedCount(); i++)
		{
			emit("pushq " + registers.getCallerSavedReg(i),
					"Save caller save register " + i);
		}
		emit("subq $32, %rsp", "Create shadow space on the stack");
		for (int i = 0; i < registers.numCallParams()
				&& i < currentFunc.getParams().size(); i++)
		{
			emitMove(registers.getParamReg(i).toString(),
					registers.getParamSpill(registers.getParamReg(i)).toString(),
					"Save param register " + i);
		}
	}

	private void restoreCallerSaveRegisters()
	{
		for (int i = 0; i < registers.numCallParams()
				&& i < currentFunc.getParams().size(); i++)
		{
			emitMove(registers.getParamSpill(registers.getParamReg(i)).toString(),
					registers.getParamReg(i).toString(), "Restore param register "
							+ i);
		}
		emit("addq $32, %rsp", "Clean up shadow stack space");
		for (int i = registers.getCallerSavedCount() - 1; i >= 0; i--)
		{
			emit("popq " + registers.getCallerSavedReg(i),
					"Restore caller save register " + i);
		}

	}

	private void assignCallParameters(List<Expression> params, int startingRegister)
	{

		int paramCount = 0;
		int maxParamRegister;
		if (params.size() + startingRegister < registers.numCallParams())
			maxParamRegister = params.size() + startingRegister - 1;
		else
			maxParamRegister = registers.numCallParams() - 1;

		for (paramCount = maxParamRegister; paramCount >= startingRegister; paramCount--)
		{
			params.get(paramCount - startingRegister).accept(this);
			if (registers.isAssignedParam(currentValue))
			{
				if (!currentValue.toString().equals(
						registers.getParamReg(paramCount).toString()))
					emitMove(registers.getParamSpill(currentValue).toString(),
							registers.getParamReg(paramCount).toString(),
							"Assign parameter from shadow space ");
			}
			else
				emit("movq " + valueString(currentValue) + ", "
						+ registers.getParamReg(paramCount), "Assign "
						+ idString(currentValue) + " to param register "
						+ (paramCount));
		}

		for (paramCount = maxParamRegister + 1; paramCount < params.size(); paramCount++)
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
			emitMove(registers.getReservedRegister().toString(),
					assignTarget.toString(), "Assign return value to "
							+ idString(assignTarget));
		emitComment("End call " + call.getId());
	}

	@Override
	public void visit(SysCall call)
	{
		saveCallerSaveRegisters();
		emit("movq $print_num, " + registers.getParamReg(0),
				"Move address of string '%d ' to param register 0");

		assignCallParameters(call.getParameters(), 1);
		if (call.getId().equals("print") || call.getId().equals("println"))
		{
			emit("call printf");
		}

		if (call.getId().equals("println"))
		{
			emit("movq $newline, " + registers.getParamReg(0),
					"Move address of string '\\n\\0' to param register 0");
			emit("call printf", "Print new line");
		}
		restoreCallerSaveRegisters();
	}

	private Value currentValue;

	@Override
	public void visit(Identifier i)
	{
		Value src = registers.value(i.getId());
		if (dest != null)
		{
			if (dest instanceof StackOffset && src instanceof StackOffset)
			{
				emit("movq " + valueString(src) + ", "
						+ registers.getReservedRegister().toString(),
						"Move stack location to reserved register.");
				emit("movq " + registers.getReservedRegister().toString() + ", "
						+ valueString(currentValue), "Assign reserved register to "
						+ idString(currentValue));
			}
			else
			{
				emit("movq " + valueString(i.getId()) + ", "
						+ valueString(currentValue), "Assign " + i.getId() + " to "
						+ idString(currentValue));
			}
			dest = null;
		}
		currentValue = src;
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
		emit("movq " + valueString(currentValue) + ", "
				+ registers.getReservedRegister(),
				"Load array index to reserved register");
		emit("imulq $" + registers.wordSize() + ", "
				+ registers.getReservedRegister(),
				"Multiply array index by WORD_SIZE");
		emit("addq $" + registers.wordSize() + ", "
				+ registers.getReservedRegister(),
				"Add WORD_SIZE to pointer to account for size word");
		a.getReference().accept(this);
		emit("addq " + valueString(currentValue) + ", "
				+ registers.getReservedRegister(), "Add array address to offset");

		if (assignTarget != null)
			emit("movq (" + registers.getReservedRegister() + "), "
					+ valueString(assignTarget), "Assign array element to "
					+ idString(assignTarget));
		else
			emit("movq " + valueString(src) + ", ("
					+ registers.getReservedRegister() + ")", "Assign "
					+ idString(src) + " to array element.");
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
		emit("movq " + valueString(currentValue) + ", " + registers.getParamReg(0),
				"Load size from " + idString(currentValue) + " to param register 1");
		emit("imulq $" + registers.wordSize() + ", " + registers.getParamReg(0),
				"Multiply number of elements by WORD_SIZE");
		emit("addq $" + registers.wordSize() + ", " + registers.getParamReg(0),
				"Request WORD_SIZE additional bytes to store size");
		emit("call malloc");
		restoreCallerSaveRegisters();
		emit("movq " + valueString(currentValue) + ", " + "("
				+ registers.getReservedRegister() + ")",
				"Store array length at zeroth location");
		if (assignTarget != null)
			emit("movq " + registers.getReservedRegister() + ", "
					+ valueString(assignTarget), "Assign new array to "
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
		emit("movq " + valueString(currentValue) + ", "
				+ registers.getReservedRegister(),
				"Move address of record to reserved register 1");
		if (offset > 0)
			emit("addq $" + offset + ", " + registers.getReservedRegister(),
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
					+ registers.getParamReg(0));
			emit("call malloc");
			restoreCallerSaveRegisters();
			if (dest != null)
				emit("movq " + registers.getReservedRegister() + ", "
						+ valueString(dest), "Assign address of record to "
						+ idString(dest));
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
		if (leftOperand instanceof StackOffset
				&& rightOperand instanceof StackOffset)
		{
			emit("movq " + valueString(rightOperand) + ", "
					+ registers.getReservedRegister(),
					"Move operand to reserved register.");
			emit("cmp " + registers.getReservedRegister() + ", "
					+ valueString(leftOperand), "Compare reserved register and "
					+ idString(rightOperand));

		}
		else
			emit("cmp " + valueString(rightOperand) + ", "
					+ valueString(leftOperand), "Compare operand "
					+ idString(leftOperand) + " and " + idString(rightOperand));
		if (assignTarget != null)
		{
			emit(getJumpInstruction(r.getOp()) + " relational_true_"
					+ relationalCount);
			emitLabel("relational_false_" + relationalCount);

			emit("movq $0, " + valueString(assignTarget), "Assigning 'false' to "
					+ idString(assignTarget));
			emit("jmp relational_end_" + relationalCount);
			emitLabel("relational_true_" + relationalCount);

			emit("movq $1, " + valueString(assignTarget), "Assigning 'true' to "
					+ idString(assignTarget));
			emitLabel("relational_end_" + relationalCount);
			currentValue = assignTarget;
		}
	}

	@Override
	public void visit(ArrayLength a)
	{
		Value assignTarget = dest;
		dest = null;
		a.getExpression().accept(this);
		if (assignTarget != null)
		{
			emitMove(valueString(currentValue), registers.getReservedRegister()
					.toString(), "Loading array reference into reserved register 0.");
			emitMove("(" + registers.getReservedRegister() + ")",
					valueString(assignTarget), "Assign array length to "
							+ idString(assignTarget));
		}
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

		emit("je " + j.getLabel().getLabel());

	}

	@Override
	public void visit(Jump j)
	{
		emit("jmp " + j.getLabel().getLabel(), "Uncondintional jump to "
				+ j.getLabel().toString());
	}
}
