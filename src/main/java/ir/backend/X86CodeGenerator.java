package ir.backend;

import java.io.PrintStream;
import java.util.List;

import ir.ArrayAccess;
import ir.Assignment;
import ir.IntegerLiteral;
import ir.NewArray;
import ir.RecordAccess;
import ir.RecordDeclaration;
import ir.Value;
import ir.BasicBlock;
import ir.BinOp;
import ir.Call;
import ir.CodePoint;
import ir.Frame;
import ir.Identifier;
import ir.SysCall;
import ir.Temporary;
import ir.visitor.IrVisitor;

public class X86CodeGenerator implements IrVisitor
{
	private PrintStream out;

	public X86CodeGenerator(PrintStream out)
	{
		this.out = out;
	}

	private void emit(String text)
	{
		out.println(text);
	}

	private void emitLabel(String text)
	{
		out.println(text + ":");
	}

	private void emitComment(String text)
	{
		out.println("#" + text);
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
		emit("pushl %ebp");
		emit("movl %esp, %ebp");
		emit("call ___main   #Call c library main");
		emit("call " + startFrameId + "  #call the starting frame");
		emit("leave");
		emit("ret");

	}

	private boolean startFrame;
	private Frame currentFrame;

	@Override
	public void visit(Frame f)
	{
		if (!startFrame)
		{
			startFrame = true;
			initFile(f.getId());
		}

		out.println();
		emitLabel(f.getId());
		emit("pushl %ebp");
		emit("movl %esp, %ebp");
		currentFrame = f;
		f.getStartingBlock().accept(this);
		emit("movl %ebp, %esp");
		emit("leave");
		emit("ret");
		out.println();

	}

	@Override
	public void visit(BasicBlock b)
	{
		for (CodePoint codePoint : b.getCodePoints())
			codePoint.accept(this);
	}

	@Override
	public void visit(CodePoint c)
	{
		c.getOperation().accept(this);
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
			throw new RuntimeException("Unrecognized op.");
		}
	}

	public void visit(BinOp b)
	{
		if(!(b.getSrc1() instanceof Temporary))
			b.getSrc1().accept(this);
		if(!(b.getSrc2() instanceof Temporary))
			b.getSrc2().accept(this);
		
		emit("popl %ebx");
		emit("popl %eax");		
		emit(getOpOpcode(b.getOp()) + " %ebx, %eax");
		emit("pushl %eax");
	}

	@Override
	public void visit(Call call)
	{
		for (Value param : call.getParameters())
		{
			param.accept(this);
		}

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
			for (Value param : call.getParameters())
			{
				param.accept(this);
				emit("pushl $print_num");
				emit("call _printf");
				emit("addl $8, %esp   # Pop _printf params off of stack");
			}
		}
		
		if(call.getId().equals("println"))
		{
	    	emitComment("Print new line");
	    	emit("pushl $newline");
	    	emit("call _printf");
	    	emit("Addl $4, %esp");
	    	emitComment("End println");
		}

	}

	private int getIndexById(List<Identifier> ids, String id)
	{
		int result = -1;
		for (int i = 0; i < ids.size(); i++)
			if (ids.get(i).getName().equals(id))
				result = i;
		return result;
	}

	@Override
	public void visit(Identifier i)
	{
		int paramIndex = getIndexById(currentFrame.getParams(), i.getName());
		if (paramIndex > 0)
		{
			emit("push " + (4 * paramIndex + 12) + "(%ebp)");
		}

		int localIndex = getIndexById(currentFrame.getLocals(), i.getName());
		if (localIndex > 0)
		{
			emit("push -" + (4 * localIndex) + "(%ebp)");
		}
	}

	@Override
	public void visit(IntegerLiteral l)
	{
		emit("pushl $" + l.getValue());
	}

	@Override
	public void visit(Temporary t)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayAccess a)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NewArray n)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RecordDeclaration r)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RecordAccess r)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Assignment assignment)
	{
		// TODO Auto-generated method stub
		
	}
}
