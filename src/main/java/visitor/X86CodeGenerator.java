package visitor;

import java.io.PrintStream;

import symboltable.RamClass;
import symboltable.RamMethod;
import symboltable.RamVariable;
import symboltable.Table;
import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.ArrayLookup;
import syntaxtree.Assign;
import syntaxtree.Call;
import syntaxtree.ClassDeclSimple;
import syntaxtree.Equality;
import syntaxtree.False;
import syntaxtree.ForEach;
import syntaxtree.Identifier;
import syntaxtree.IdentifierExp;
import syntaxtree.If;
import syntaxtree.IntegerLiteral;
import syntaxtree.LessThan;
import syntaxtree.LessThanOrEqual;
import syntaxtree.MainClass;
import syntaxtree.MethodDecl;
import syntaxtree.Minus;
import syntaxtree.NewArray;
import syntaxtree.NewObject;
import syntaxtree.Not;
import syntaxtree.Or;
import syntaxtree.Plus;
import syntaxtree.PlusEquals;
import syntaxtree.Print;
import syntaxtree.PrintLn;
import syntaxtree.Program;
import syntaxtree.This;
import syntaxtree.Times;
import syntaxtree.True;
import syntaxtree.While;

public class X86CodeGenerator extends DepthFirstVisitor
{
	private PrintStream out;
	private Table symTable;	
	
	public X86CodeGenerator(PrintStream out, Table table)
	{
		this.out = out;
		this.symTable = table;
	}
	
    private void emit(String s)
    {
        out.println("\t" + s);
    }

    private void emitLabel(String l)
    {
        out.println(l + ":");
    }

    private void emitComment(String s)
    {
        out.println("\t" + "#" + s);
    }
    
    @Override
    public void visit(Program p)
    {
    	emitComment("General constants used for output");
    	emitLabel("print_num");
    	emit(".ascii \"%d \\0\"");
    	emitLabel("newline");
    	emit(".ascii \"\\n\\0\"");
    	
    	p.m.accept(this);
    	
    	for(int i = 0; i < p.cl.size(); i++)
    	{
    		p.cl.elementAt(i).accept(this);
    	}
    }
    
    @Override
    public void visit(MainClass m)    
    {
    	emit(".globl _main");    	
    	emitLabel("_main");
    	emitComment("Prologue to _main");
    	emit("pushl %ebp");    	
    	emit("movl %esp, %ebp");
    	emit("call ___main   #Call c library main");
    	m.s.accept(this);
    	emit("leave");
    	emit("ret");
    }
    
    @Override
    public void visit(IntegerLiteral i)
    {
    	emit("pushl $" + i.i);
    }
    
    @Override 
    public void visit(True t)
    {
    	emit("pushl $1");
    }
    
    public void visit(False t)
    {
    	emit("pushl $0");
    }
    
    @Override
    public void visit(Print p)
    {
    	for(int i = 0; i < p.e.size(); i++)
    	{
    		p.e.elementAt(i).accept(this);
    		emit("pushl $print_num");
    		emit("call _printf");
    		emit("addl $8, %esp   # Pop _printf params off of stack");
    	}
    }
    
    @Override
    public void visit(PrintLn p)
    {
    	emitComment("Begin println");
    	for(int i = 0; i < p.list.size(); i++)
    	{
    		p.list.elementAt(i).accept(this);
    		emit("pushl $print_num");
    		emit("call _printf");
    		emit("addl $8, %esp   #Pop _printf params off of stack");
    	}
    	
    	emitComment("Print new line");
    	emit("pushl $newline");
    	emit("call _printf");
    	emit("Addl $4, %esp");
    	emitComment("End println");
    
    }    
    
    @Override
    public void visit(Plus p)
    {   
    	emitComment("Begin Plus");
    	p.e1.accept(this);
    	p.e2.accept(this);
    	emit("popl %ebx");
    	emit("popl %eax");
    	emit("addl %eax, %ebx");
    	emit("pushl %ebx");
    	emitComment("End plus");
    }
    
    @Override
    public void visit(Minus m)
    {   
    	emitComment("Begin Minus");
    	m.e1.accept(this);
    	m.e2.accept(this);
    	emit("popl %ebx");
    	emit("popl %eax");
    	emit("subl %ebx, %eax");
    	emit("pushl %eax");
    	emitComment("End minus");
    }    
    
    @Override 
    public void visit(Times t)
    {
    	emitComment("Begin Times");
    	t.e1.accept(this);
    	t.e2.accept(this);
    	emit("popl %ebx");
    	emit("popl %eax");
    	emit("imull %eax, %ebx");
    	emit("pushl %ebx");
    	emitComment("End times");
    	
    }
    
    private int ifCounter;
    @Override 
    public void visit(If i)
    {
    	int ifCounter = ++this.ifCounter;    	
    	emitComment("Begin if");
    	i.e.accept(this);
    	emit("popl %eax");
    	emit("movl $1, %ebx");
    	emit("cmp %ebx, %eax");
    	emit("je if_true_" + ifCounter);
    	
    	//Else case first
    	i.s2.accept(this);
    	emit("jmp if_done_" + ifCounter);
    	
    	//Then case
    	emitLabel("if_true_" + ifCounter);
    	i.s1.accept(this);
    	
    	emitLabel("if_done_" + ifCounter);
    	
    	emitComment("End if");
    }
    
    private int andCounter;
    @Override
    public void visit(And a)
    {
    	int andCounter = ++this.andCounter;
    	emitComment("Begin and");
    	a.e1.accept(this);
    	emit("popl %eax");
    	emit("movl $0, %ebx");
    	emit("cmp %ebx, %eax");
    	emit("je and_false_" + andCounter);
    	
    	a.e2.accept(this);
    	emit("popl %eax");
    	emit("movl $1, %ebx");
    	emit("cmp %ebx, %eax");
    	emit("je and_true_" + andCounter);
    	
    	emitLabel("and_false_" + andCounter);
    	emit("pushl $0");
    	emit("jmp and_done_" + andCounter);
    	emitLabel("and_true_" + andCounter);
    	emit("pushl $1");
    	
    	emitLabel("and_done_" + andCounter);
    	emitComment("End and");
    }
    
    private int orCounter;
    @Override
    public void visit(Or o)
    {
    	int orCounter = ++this.orCounter;
    	emitComment("Begin or");
    	o.e1.accept(this);
    	emit("popl %eax");
    	emit("movl $1, %ebx");
    	emit("cmp %ebx, %eax");
    	emit("je or_true_" + orCounter);
    	
    	o.e2.accept(this);
    	emit("popl %eax");
    	emit("movl $0, %ebx");
    	emit("cmp %ebx, %eax");
    	emit("je or_false_" + andCounter);
    	
    	emitLabel("or_false_" + andCounter);
    	emit("pushl $0");
    	emit("jmp or_done_" + andCounter);
    	emitLabel("or_true_" + andCounter);
    	emit("pushl $1");
    	
    	emitLabel("or_done_" + andCounter);
    	emitComment("End or");
    }    
        
    @Override
    public void visit(Not n)
    {
    	n.e.accept(this);
    	emit("popl %eax");
    	emit("negl %eax");
    	emit("incl %eax");
    	emit("pushl %eax");    	
    }
    
    private int equalCounter;
    public void visit(Equality e)    
    {
    	e.e1.accept(this);
    	e.e2.accept(this);
    	int equalCounter = ++this.equalCounter;
    	String isDoneLabel = "equality_done_" + equalCounter;
    	String isEqualLabel = "equaliy_is_equal_" + equalCounter;
    	
    	emit("pop %eax	# Result of left of eq expression");
    	emit("pop %ebx  # Result of right of eq expression");
    	emit("cmp %eax, %ebx # Compare the results");
    	emit("je " + isEqualLabel);
    	emitComment("Not equal case");
    	emit("push $0   # Push false");
    	emit("jmp " + isDoneLabel);
    	emitComment("Equal case");
    	emitLabel(isEqualLabel);
    	emit("push $1");
    	emitLabel(isDoneLabel);
    }
    
    private int lessThanCounter;
    @Override
    public void visit(LessThan l)
    {
    	int lessThanCounter = ++this.lessThanCounter;
    	l.e1.accept(this);
    	l.e2.accept(this);
    	emit("popl %ebx");
    	emit("popl %eax");
    	emit("cmp %ebx, %eax");
    	emit("jl less_than_" + lessThanCounter);
    	emit("pushl $0");
    	emit("jmp less_than_done_" + lessThanCounter);
    	emitLabel("less_than_" + lessThanCounter);
    	emit("pushl $1");
    	emitLabel("less_than_done_" + lessThanCounter);
    }
    
    private int lessThanOrEqualCounter;
    @Override
    public void visit(LessThanOrEqual l)
    {
    	int lessThanOrEqualCounter = ++this.lessThanOrEqualCounter;
    	String lessThanDoneLabel = "less_than_equal_done_" + lessThanOrEqualCounter;
    	String lessThanLabel = "less_than_" + lessThanOrEqualCounter;
    	l.e1.accept(this);
    	l.e2.accept(this);
    	emit("popl %ebx");
    	emit("popl %eax");
    	emit("cmp %ebx, %eax");
    	emit("jle " + lessThanLabel);
    	emit("pushl $0");
    	emit("jmp " + lessThanDoneLabel);
    	emitLabel(lessThanLabel);
    	emit("pushl $1");
    	emitLabel(lessThanDoneLabel);
    }    
    
    private RamClass currentClass;
    @Override
    public void visit(ClassDeclSimple c)
    {
    	currentClass = symTable.getClass(c.i.s);
    	
    	for(int i = 0; i < c.vl.size(); i++)
    	{
    		currentClass.getVar(c.vl.get(i).i.s).setMemoryOffset(i * 4);    		
    	}
    
    	for(int i = 0; i < c.ml.size(); i++)
    	{
    		c.ml.elementAt(i).accept(this);
    	}
    }    

    
    private RamMethod currentMethod;
    @Override
    public void visit(MethodDecl m)
    {
    	currentMethod = symTable.getMethod(m.i.s, currentClass.getId());
    	emitLabel(m.i.s);
    	emit("pushl %ebp");
    	emit("movl %esp, %ebp");
    	
    	int varSpace = m.vl.size() * 4;
    	emit("subl $" + varSpace + ", %esp   # Allocate stack space for " + m.vl.size() + " varaibles" );
    	
    	
    	for(int i = 0; i < m.vl.size(); i++)
    	{
    		currentMethod.getVar(m.vl.get(i).i.s).setMemoryOffset((i * 4 + 4) * -1);
    	}
    	
    	for(int i = 0; i < m.sl.size(); i++)
    	{
    		m.sl.get(i).accept(this);
    	}
    	
    	m.e.accept(this);
    	emit("popl %eax");
    	emit("movl %ebp, %esp");  
    	emit("leave");
    	emit("ret");    	
    }
    
    private void saveCallerRegisters()
    {
    	emit("pushl %eax");
    	emit("pushl %ecx");
    	emit("pushl %edx");
    }
    
    private void restoreCallerRegisters()
    {
    	emit("popl %edx");
    	emit("popl %ecx");
    	emit("popl %eax");
    }    
    
    @Override
    public void visit(Call c)
    {
    	emit("subl $4, %esp   #Save space for return value");
    	saveCallerRegisters();
    	    	
    	for(int i = c.el.size() - 1; i >= 0; i--)
    	{
    		c.el.elementAt(i).accept(this);
    	}    	
    	c.e.accept(this);    	
    	emit("call " + c.i);
    	emit("addl $" + (c.el.size() * 4 + 4) + ", %esp");    	
    	emit("movl %eax, 12(%esp)");
    	restoreCallerRegisters();
    	
    }
    
    @Override
    public void visit(Identifier i)
    {
    	//First check the method
    	RamVariable var = currentMethod.getVar(i.s);
    	if(var != null)
    	{    	
    		emit("lea " + var.getMemoryOffset() + "(%ebp), %eax");
    		emit("pushl %eax");
    		return;
    	}
    	
    	var = currentMethod.getParam(i.s);
    	if(var != null)
    	{
    		int index = currentMethod.getParamIndex(i.s);
    		emit("lea " + (index * 4 + 12) + "(%ebp), %eax");
    		emit("pushl %eax");
    		return;
    	}
    	
    	//Finally, instance variable
    	var = currentClass.getVar(i.s);
    	if(var != null)
    	{    		
    	
    		emit("movl 8(%ebp), %eax");
    		emit("addl $" + (var.getMemoryOffset()) + ", %eax");
    		emit("pushl %eax");
    	}    	
    }
    
    @Override
    public void visit(IdentifierExp i)
    {
    	//First check the method
    	RamVariable var = currentMethod.getVar(i.s);
    	if(var != null)
    	{    		
    		emit("pushl " + var.getMemoryOffset() + "(%ebp)");
    		return;
    	}
    	
    	//Next parameter
    	var = currentMethod.getParam(i.s);
    	if(var != null)
    	{
    		int index = currentMethod.getParamIndex(i.s);
    		emit("pushl " + (index * 4 + 12) + "(%ebp)");
    		return;
    	}
    	
    	//Finally, instance variable
    	var = currentClass.getVar(i.s);
    	if(var != null)
    	{   
    		emit("movl 8(%ebp), %ecx");
    		emit("pushl " + var.getMemoryOffset() + "(%ecx)");
    	}
    }
    
    @Override
    public void visit(Assign a)
    {
    	a.e.accept(this);
    	a.i.accept(this);
    	emit("popl %eax");
    	emit("popl (%eax)");
    }    
    
    @Override 
    public void visit(PlusEquals p)
    {
    	p.e.accept(this);
    	p.id.accept(this);
    	emit("popl %eax  # Load Added of variable to assign");
    	emit("popl %ebx  # Results of rvalue expression");
    	emit("movl (%eax), %ecx");
    	emit("addl %ecx, %ebx");
    	emit("movl %ebx, (%eax)");
    }
    
    @Override 
    public void visit(NewObject n)
    {
    	RamClass obj = symTable.getClass(n.i.s);
    	int size = obj.getVarCount() * 4;
    	if(size > 0)
    	{
	    	emit("pushl $" + size + "   # Push param for malloc call onto stack");
	    	
	    	emit("call _malloc");
	    	emit("addl $4, %esp    # Clear stack from malloc call ");
	    	emit("pushl %eax");
    	}
    	else    		
    		emit ("pushl $0      # Place holder address for zero sized objects");
    }
    
    @Override
    public void visit(This t)
    {		
    	emit("pushl 8(%ebp)");
    }
    
    @Override
    public void visit(NewArray n)
    {
    	n.e.accept(this);
    	emit("popl %ebx         # Size of array");    	    	
    	emit("movl $4, %eax     # Number of bytes for each element");
    	emit("imull %ebx, %eax  # Total size of array");
    	emit("addl $4, %eax     # Add four bytes for size");
    	emit("pushl %eax        # Push total size onto stack");
    	emit("call _malloc");
    	emit("addl $4, %esp     # clear stack from malloc call");
    	emit("movl %ebx, (%eax) # Store size in first four bytes");
    	emit("pushl %eax        # Push address of new array onto stack");
    	
    	
    }
    
    @Override
    public void visit(ArrayAssign a)
    {
    	a.e2.accept(this);
    	a.e1.accept(this);
    	a.i.accept(this);
    	
    	emit("popl %esi         # Pop address of array");
    	emit("movl (%esi), %esi # Dereference pointer");
    	emit("popl %edi         # Pop index of assignment");
    	emit("imull $4, %edi    # Compute size of offset");
    	emit("addl $4, %edi     # Account for size word");
    	emit("addl %edi, %esi   # Add offset to base address of array");
    	emit("popl (%esi)       # Pop value to be assigned");
    }
    
    @Override
    public void visit(ArrayLookup a)
    {
    	a.e2.accept(this);
    	a.e1.accept(this);
    	emit("popl %esi");
    	emit("popl %edi");
    	emit("imull $4, %edi ");
    	emit("addl %edi, %esi");
    	emit("pushl 4(%esi)");    	
    }
    
    int whileCounter;
    @Override    
    public void visit(While e)
    {
    	++this.whileCounter;
    	String  testLabel = "while_test_" + whileCounter; 
    	String doneLabel = "while_done_" + whileCounter;
    	emitComment("While loop");
    	emitLabel(testLabel);
    	e.e.accept(this);
    	emit("movl $0, %ebx     # Move false value for comparison into register");
    	emit("popl %eax         # Pop result of boolean expresion");
    	emit("cmp %eax, %ebx");
    	emit("je " + doneLabel );
    	e.s.accept(this);
    	emit("jmp " + testLabel);
    	emitLabel(doneLabel);
    }
    
    int forEachCounter;
    @Override
    public void visit(ForEach e)
    {
    	int forEachCounter = ++this.forEachCounter;
    	e.iterator.accept(this);
    	emitComment("Load size for counter");
    	//emit("pop "
    	
    	
    }
}
