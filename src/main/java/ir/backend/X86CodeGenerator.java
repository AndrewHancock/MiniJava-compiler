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
import ir.ops.IntegerLiteral;
import ir.ops.Jump;
import ir.ops.Label;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.Statement;
import ir.ops.SysCall;
import ir.regalloc.RegisterAllocator;
import ir.regalloc.Value;
import ir.visitor.IrVisitor;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class X86CodeGenerator implements IrVisitor
{
	private PrintStream out;
	private HashMap<String, RecordDeclaration> recordMap = new HashMap<String, RecordDeclaration>();

	private String[] registers = { "rcx", "rdx", "r8", "r9", "r10", "r11", "r12",
			"r13", "r14", "r15" };

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
	private FunctionDeclaration currentFunction;
	private RegisterAllocator allocator = new RegisterAllocator(registers.length);

	@Override
	public void visit(FunctionDeclaration f)
	{
		allocator.clear();
		if (!startFrame)
		{
			startFrame = true;
			initFile(f.getId());
		}

		out.println();
		String nameNames = f.getNamespace() + "_";
		emitLabel(nameNames + f.getId());
		emit("pushl %rbp");
		emit("movl %rsp, %rbp");
		currentFunction = f;

		for (Identifier id : f.getLocals())
			allocator.addIdentifier(id.getId());
		for (Identifier id : f.getTemporaries())
			allocator.addIdentifier(id.getId());

		emit("subl $" + allocator.getStackSize() + " , %rsp",
				"Reserve spsace for locals and temporaries.");

		for (Statement statement : f.getStatements())
			statement.accept(this);

		emit("leave");
		emit("ret");
		out.println();
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
			emit("movl " + right + " ,  %rax");
			emit(getOpOpcode(b.getOp()) + " " + left + ", %rax");
		}
	}

	@Override
	public void visit(Call call)
	{
		for (int i = call.getParameters().size() - 1; i >= 0; i--)
			call.getParameters().get(i).accept(this);

		emit("call " + call.getNamespace() + "_" + call.getId());
		int paramSize = call.getParameters().size() * 4;
		if (paramSize > 0)
			emit("addl $" + paramSize + ", %esp   # Clean up parameters from call");
		emit("push %eax  #Store results of call onto stack");
	}

	@Override
	public void visit(SysCall call)
	{
		if (call.getId().equals("print") || call.getId().equals("println"))
		{
			for (Expression param : call.getParameters())
			{
				param.accept(this);
				emit("pushl $print_num");
				emit("call _printf");
				emit("addl $8, %esp   # Pop _printf params off of stack");
			}
		}

		// Note: Leave the return value on the stack
		if (call.getId().equals("println"))
		{
			emitComment("Print new line");
			emit("pushl $newline");
			emit("call _printf");
			emitComment("End println");
		}

	}

	private int getIdByIndex(List<Identifier> ids, String id)
	{
		int result = -1;
		for (int i = 0; i < ids.size(); i++)
			if (ids.get(i).getId().equals(id))
				result = i;
		return result;
	}

	private int getIdentifierStackOffset(String id)
	{
		int paramIndex;
		int localIndex;
		int tempIndex;
		if ((paramIndex = getIdByIndex(currentFunction.getParams(), id)) >= 0)
		{
			return 4 * paramIndex + 8;
		}
		else if ((localIndex = getIdByIndex(currentFunction.getLocals(), id)) >= 0)
		{
			return -(4 * localIndex + 4);
		}
		else if ((tempIndex = getIdByIndex(currentFunction.getTemporaries(), id)) >= 0)
		{
			return -(tempIndex * 4 + currentFunction.getLocals().size() * 4 + 4);
		}
		else
			return -1;

	}

	private boolean rValue = true;
	private Value currentValue;

	@Override
	public void visit(Identifier i)
	{
		currentValue = allocator.getValueForIdentifier(i.getId());
	}

	@Override
	public void visit(IntegerLiteral l)
	{
		emit("pushl $" + l.getValue());
	}

	@Override
	public void visit(ArrayAccess a)
	{
		boolean currentRValue = rValue;
		emitComment("Access an array element");
		rValue = true;
		a.getReference().accept(this);

		emit("popl %ebx   #Load array reference");
		rValue = true;
		a.getIndex().accept(this);
		rValue = currentRValue;
		emit("popl %eax   #Load array index");
		emit("imul $4, %eax");
		emit("addl %ebx, %eax");
		if (rValue)
			emit("push (%eax)    # Store contents of value at offset onto stack");
		else
		{
			rValue = true;
			emit("push %eax");
		}
	}

	@Override
	public void visit(ArrayAllocation n)
	{
		emitComment("Allocate new array");
		n.getSize().accept(this);
		emit("pop %eax");
		emit("mov %eax, %ebx", "Save size before _malloc call");
		emit("imull $4, %eax");
		emit("addl $4, %eax", "Request 4 additional bytes for size");
		emit("push %eax");
		emit("call _malloc");
		emit("mov %ebx, (%eax)", "Store array length at zeroth location");
		emit("push %eax");
	}

	@Override
	public void visit(RecordDeclaration r)
	{
		recordMap.put(r.getNamespace() + r.getId(), r);
	}

	@Override
	public void visit(RecordAccess r)
	{
		int offset = (r.getFieldIndex() * 4);
		boolean isRValue = rValue;
		rValue = true;
		r.getIdentifier().accept(this);
		rValue = isRValue;
		emit("pop %esi");
		if (rValue)
			emit("push " + offset + "(%esi)");
		else
		{
			if (offset > 0)
				emit("addl $" + offset + ", %esi");
			emit("push %esi");
			rValue = true;
		}
	}

	@Override
	public void visit(Assignment assignment)
	{
		rValue = true;
		assignment.getSrc().accept(this);
		rValue = false;
		assignment.getDest().accept(this);
		emit("pop %eax");
		emit("pop %ebx");
		emit("movl %ebx, (%eax)");
	}

	@Override
	public void visit(RecordAllocation a)
	{
		RecordDeclaration decl = recordMap.get(a.getNamespace() + a.getTypeId());
		if (decl.getFieldCount() > 0)
		{
			emit("push $" + (decl.getFieldCount() * 4)
					+ "     # Push object size onto stack");
			emit("call _malloc");
			emit("addl $4, %esp");
			emit("push %eax");
		}
		else
			emit("push $0      # Push placeholder address onto stack");
	}

	@Override
	public void visit(Return r)
	{
		rValue = true;
		r.getSource().accept(this);
		emit("popl %eax", "Pop return value");
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
		r.getSrc2().accept(this);
		emit("Popl %ebx", "Pop second operand");
		emit("popl %eax", "Pop first operand");
		emit("cmp %ebx, %eax", "Compare operands");
		emit(getJumpInstruction(r.getOp()) + " relational_true_" + relationalCount);
		emitLabel("relational_false_" + relationalCount);
		emit("pushl $0");
		emit("jmp relational_end_" + relationalCount);
		emitLabel("relational_true_" + relationalCount);
		emit("pushl $1");
		emitLabel("relational_end_" + relationalCount);
	}

	@Override
	public void visit(ArrayLength a)
	{
		a.getExpression().accept(this);
		emit("popl %eax");
		emit("pushl (%eax)");
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
		emit("pop %ebx", "Pop result of condition");
		emit("cmp $1, %ebx");
		emit("je " + j.getLabel().getLabel());
	}

	@Override
	public void visit(Jump j)
	{
		emit("jmp " + j.getLabel().getLabel(), "Uncondintional jump to "
				+ j.getLabel().toString());

	}

}
