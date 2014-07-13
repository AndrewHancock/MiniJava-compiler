package ir.backend;

import java.io.PrintStream;
import java.util.List;

import ir.Alias;
import ir.BasicBlock;
import ir.BinOp;
import ir.Call;
import ir.CodePoint;
import ir.Frame;
import ir.Identifier;
import ir.SysCall;
import ir.Temporary;
import ir.visitor.DepthFirstIrVisitor;

public class X86CodeGenerator extends DepthFirstIrVisitor
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
    	emit("call " + startFrameId + " call the starting frame");
    	emit("leave");
    	emit("ret");
    	
	}
	
	private boolean startFrame;
	private Frame currentFrame;
	@Override
	public void visit(Frame f)
	{		
		if(!startFrame)
		{
			startFrame = true;
			initFile(f.getId());			
		}
		
		emitLabel(f.getId());
    	emit("pushl %ebp");
    	emit("movl %esp, %ebp");
    	currentFrame = f;
		f.getStartingBlock().accept(this);	
    	emit("movl %ebp, %esp");  
    	emit("leave");
    	emit("ret");  
		
	}

	@Override
	public void visit(BasicBlock b)
	{		
		for(CodePoint codePoint : b.getCodePoints())
			codePoint.accept(this);
	}

	@Override
	public void visit(CodePoint c)
	{	
		c.getOperation().accept(this);
	}
	
	public void visit(BinOp b)
	{
		
	}
	
	
	@Override
	public void visit(Call call)
	{
		
		for(Alias param : call.getParameters())
		{
			param.accept(this);
		}
		
		int paramSize = call.getParameters().size() * 4;
		if(paramSize > 0)
			emit("addl $" + paramSize + ", %esp   # Clean up parameters from call");
		
		emit("push %eax  #Store results of call onto stack");
	}
	
	@Override
	public void visit(SysCall call)
	{		
		if(call.getId().equals("print"))
		{
			for(Alias param : call.getParameters())
			{
				param.accept(this);
	    		emit("pushl $print_num");
	    		emit("call _printf");
	    		emit("addl $8, %esp   # Pop _printf params off of stack");			
			}
		}	
	}

	@Override
	public void visit(Temporary t)
	{
		
	}
	
	private int getIndexById(List<Identifier> ids, String id)
	{
		int result = -1;
		for(int i = 0; i < ids.size(); i++)
			if(ids.get(i).getName().equals(id))
				result = i;		
		return result;			
	}

	@Override
	public void visit(Identifier i)
	{
		int paramIndex = getIndexById(currentFrame.getParams(), i.getName());
		if(paramIndex > 0 )
		{			
			emit("push " + (4 * paramIndex + 12) + "(%ebp)");
		}
		
		int localIndex = getIndexById(currentFrame.getLocals(), i.getName());
		if(localIndex > 0)
		{
			emit("push -" + (4 * localIndex) + "(%ebp)");
		}	
	}
	

}
