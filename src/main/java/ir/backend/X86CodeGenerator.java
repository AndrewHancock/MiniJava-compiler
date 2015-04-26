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
import ir.regalloc.Register;
import ir.regalloc.RegisterAllocator;
import ir.regalloc.StackOffset;
import ir.regalloc.Value;
import ir.visitor.IrVisitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class X86CodeGenerator implements IrVisitor
{
	private PrintStream out;
	private HashMap<String, RecordDeclaration> recordMap = new HashMap<String, RecordDeclaration>();

	private final int WORD_SIZE = 8;

	private String[] callParamRegisters = { "rcx", "rdx", "r8", "r9" };
	private String[] calleeSaveRegisters = { "r12", "r13",
			"r14", "r15", "rdi", "rsi" };
	private String[] callerSaveRegisters = { "r10", "r11" };
	private String[] reservedRegisters = { "rax", "rbx" };

	public X86CodeGenerator(PrintStream out)
	{
		this.out = out;
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

		emit(".globl _main");
		emitLabel("_main");
		emitComment("Prologue to _main");
		emit("pushl %rbp");
		emit("movl %rsp, %rbp");
		emit("call ___main   #Call c library main");
		emit("call " + startFrameId + "  #call the starting frame");
		emit("leave");
		emit("ret");
	}

	private boolean startFrame;
	private RegisterAllocator allocator = new GraphColorAllocator();
	private Map<String, Value> idToValueMapping = new HashMap<String, Value>();
	private Map<Value, String> valueToIdMapping = new HashMap<Value, String>();

	private void allocateRegisters(FunctionDeclaration f)
	{
		Map<String, Value> callerSavedAllocation = allocator.allocateRegisters(f,
				callerSaveRegisters.length);
		if (allocator.getSpillCount() == 0)
		{
			idToValueMapping = callerSavedAllocation;
		}
		else
		{
			idToValueMapping = allocator.allocateRegisters(f,
					calleeSaveRegisters.length + callerSaveRegisters.length);
		}
		assignInputParameters(f.getParams());
		for (Entry<String, Value> entry : idToValueMapping.entrySet())
		{
			valueToIdMapping.put(entry.getValue(), entry.getKey());
		}
	}

	private void assignInputParameters(List<Identifier> params)
	{
		int paramCount = 0;
		for (paramCount = 0; paramCount < params.size() 
				&& paramCount < callParamRegisters.length; paramCount++)
		{
			idToValueMapping.put(params.get(paramCount).getId(), new Register(
					callerSaveRegisters.length + calleeSaveRegisters.length + paramCount));
		}

		for (; paramCount < params.size() - 1; paramCount++)
		{
			idToValueMapping.put(params.get(paramCount).getId(), new StackOffset(
					paramCount * WORD_SIZE + 16));
		}
	}

	private String valueString(String id)
	{
		Value value = idToValueMapping.get(id);
		return valueString(value);
	}

	public String valueString(Value value)
	{
		if (value instanceof StackOffset)
			return "-" + ((StackOffset) value).getStackOffset() + "(%rsp)";
		else if (value instanceof RegisterDereference)
		{
			return "(%"
					+ reservedRegisters[((RegisterDereference) value)
							.getRegisterIndex()] + ")";
		}
		else if (value instanceof IntegerLiteral)
		{
			return "$" + ((IntegerLiteral) value).getValue();
		}
		else
		{
			int registerOffset = ((Register) value).getRegisterIndex();
			if (registerOffset < callerSaveRegisters.length)
				return "%" + callerSaveRegisters[registerOffset];
			else if (registerOffset < calleeSaveRegisters.length)
				return "%" + calleeSaveRegisters[registerOffset - callerSaveRegisters.length];
			else
				return "%" + callParamRegisters[registerOffset - callerSaveRegisters.length - calleeSaveRegisters.length];
		}
	}

	public String idString(Value value)
	{
		if (value instanceof IntegerLiteral)
		{
			return Integer.toString(((IntegerLiteral) value).getValue());
		}
		if (value instanceof RegisterDereference)
		{
			return " value at address of "
					+ reservedRegisters[((RegisterDereference) value)
							.getRegisterIndex()];
		}
		else
			return valueToIdMapping.get(value);

	}

	private void emitFunctionHeader(FunctionDeclaration f)
	{
		emitComment("Function: " + f.getNamespace() + "." + f.getId());
		emitComment("");
		emitComment("Register and Stack Allocation:");

		for (Identifier id : f.getParams())
		{
			if (idToValueMapping.containsKey(id.getId()))
				emitComment(valueString(id.getId()) + " - " + id.getId());
		}

		for (Identifier id : f.getLocals())
		{
			if (idToValueMapping.containsKey(id.getId()))
				emitComment(valueString(id.getId()) + " - " + id.getId());
			else
				emitComment("Unused - " + id.getId());
		}
		for (Identifier id : f.getTemporaries())
		{
			if (idToValueMapping.containsKey(id.getId()))
				emitComment(valueString(id.getId()) + " - " + id.getId());
			else
				emitComment("Unused - " + id.getId());
		}

	}

	@Override
	public void visit(FunctionDeclaration f)
	{
		if (!startFrame)
		{
			startFrame = true;
			initFile(f.getId());
		}
		out.println();
		allocateRegisters(f);
		emitFunctionHeader(f);
		String nameNames = f.getNamespace() + "_";
		emitLabel(nameNames + f.getId());
		emit("pushl %rbp");
		emit("movl %rsp, %rbp");

		if (allocator.getStackSize() > 0)
			emit("subl $" + allocator.getStackSize() + " , %rsp",
					"Reserve spsace for locals and temporaries.");

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
				&& paramCount < callParamRegisters.length - startingRegister; paramCount++)
		{
			params.get(paramCount).accept(this);
			emit("movq " + valueString(currentValue) + ","
					+ callParamRegisters[startingRegister + paramCount],
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

		if (params.size() > callParamRegisters.length)
		{
			int callStackUsage = (params.size() - callParamRegisters.length)
					* WORD_SIZE;
			emit("addq $" + callStackUsage + ", %rsp", "Clean up parameter stack");

		}
		int paramCount = 0;
		for (paramCount = 0; paramCount < params.size() - 1
				&& paramCount < callParamRegisters.length - startingRegister; paramCount++)
		{
			params.get(paramCount).accept(this);
			emit("movq " + valueString(currentValue) + ","
					+ callParamRegisters[paramCount + startingRegister],
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

		emit("movq $print_num, " + callParamRegisters[0],
				"Move address of string '%d ' to param register 1");

		if (call.getId().equals("print") || call.getId().equals("println"))
		{
			assignCallParameters(call.getParameters(), 1);
			emit("call _printf");
		}

		if (call.getId().equals("println"))
		{
			emitComment("Print new line");
			emit("pushl $newline");
			emit("call _printf");
		}
		restoreParameters(call.getParameters(), 1);
		emitComment("End " + call.getId());
	}

	private Value currentValue;

	@Override
	public void visit(Identifier i)
	{
		currentValue = idToValueMapping.get(i.getId());
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
			return "addl";
		case MULT:
			return "imull";
		case SUBTRACT:
			return "subl";
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

			emit("movl " + valueString(left) + " , " + valueString(dest),
					"Moving " + valueToIdMapping.get(left) + " to "
							+ valueToIdMapping.get(dest));

			emit(getOpOpcode(b.getOp()) + valueString(right) + " "
					+ valueString(left), "BinOp on " + valueToIdMapping.get(left)
					+ " and " + valueToIdMapping.get(right));
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
		emit("movq " + valueString(currentValue) + ", " + callParamRegisters[0],
				"Load size from " + idString(currentValue) + " to param register 1");
		emit("movq " + callParamRegisters[0] + ", " + reservedRegisters[0],
				"Save size before _malloc call to reserved register");
		emit("qmull $" + WORD_SIZE + ", " + callParamRegisters[0],
				"Multiply number of elements by WORD_SIZE");
		emit("addq $" + WORD_SIZE + ", " + callParamRegisters[0],
				"Request WORD_SIZE additional bytes to store size");
		emit("call _malloc");
		emit("movq " + reservedRegisters[0] + ", (%rax)",
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
		int offset = (r.getFieldIndex() * WORD_SIZE);

		out.println();
		emitComment("Record Acccess");
		r.getIdentifier().accept(this);
		emit("movq " + valueString(currentValue) + ", " + reservedRegisters[0],
				"Move address of record to reserved register 1");
		if (offset > 0)
			emit("addq " + offset + ", " + reservedRegisters[0],
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
			emit("movq $" + (decl.getFieldCount() * WORD_SIZE) + ", "
					+ callParamRegisters[0]);
			emit("call _malloc");
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
		emit("moveq " + valueString(currentValue) + ", %rax", "Move "
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
			emit("movq $0, " + reservedRegisters[0],
					"Moving 'false' to reserved register 1.");
		emit("jmp relational_end_" + relationalCount);
		emitLabel("relational_true_" + relationalCount);
		if (dest != null)
			emit("movq $1, " + valueString(dest), "Assigning 'true' to "
					+ idString(dest));
		else
			emit("movq $1, " + reservedRegisters[0],
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
		emit("cmp $1, " + valueString(currentValue), "Compare 'true' to "
				+ idString(currentValue));
		emit("je " + j.getLabel().getLabel());
	}

	@Override
	public void visit(Jump j)
	{
		emit("jmp " + j.getLabel().getLabel(), "Uncondintional jump to "
				+ j.getLabel().toString());
	}
}
