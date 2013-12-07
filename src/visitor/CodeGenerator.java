package visitor;
import syntaxtree.*;
import symboltable.*;
import visitor.error.ErrorMsg;

public class CodeGenerator extends DepthFirstVisitor
{

    private ErrorMsg errorMsg = new ErrorMsg();
    private java.io.PrintStream out;
    private Table symTable;
    private RamClass currClass;
    private RamMethod currMethod;
    private TypeCheckExpVisitor typeCheckExpVisitor;

    public CodeGenerator(java.io.PrintStream o, Table st)
    {
        out = o;
        symTable = st;
        typeCheckExpVisitor = new TypeCheckExpVisitor(errorMsg, symTable);
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
    
    private TypeCheckExpVisitor getExpVisitor()
    {
        typeCheckExpVisitor.currClass = currClass;
        typeCheckExpVisitor.currMethod = currMethod;
        return typeCheckExpVisitor;
    }    

    // MainClass m;
    // ClassDeclList cl;
    public void visit(Program n)
    {
        emit(".data");
        emitLabel("newline");
        emit(".asciiz \"\\n\"");
        emitLabel("space");
        emit(".asciiz \" \"");

        emit(".text");
        emit(".globl main");

        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++)
        {
            n.cl.elementAt(i).accept(this);
        }

    }

    // Identifier i1, i2;
    // Statement s;
    public void visit(MainClass n)
    {
        symTable.addClass(n.i1.toString());
        currClass = symTable.getClass(n.i1.toString());
        symTable.getClass(n.i1.s).addMethod("main", new IdentifierType("void"));
        currMethod = symTable.getClass(n.i1.toString()).getMethod("main");
        symTable.getMethod("main", currClass.getId()).addParam(n.i2.toString(),
                new IdentifierType("String[]"));

        emitLabel("main");

        emitComment("begin prologue -- main");
        emit("subu $sp, $sp, 32    # stack frame is at least 32 bytes");
        emit("sw $fp, 4($sp)       # save caller's frame pointer");
        emit("sw $ra, 0($sp)       # save return address");

        emit("addi $fp, $sp, 28    # set up main's frame pointer");
        emitComment("end prologue -- main");

        n.s.accept(this);

        emitComment("begin epilogue -- main");
        emit("lw $ra, 0($sp)       # restore return address");
        emit("lw $fp, 4($sp)       # restore caller's frame pointer");
        emit("addi $sp, $sp, 32    # pop the stack");
        emitComment("end epilogue -- main");
        emit("jr $ra");
        emit("\n"); // end programs with new line

        currMethod = null;

    }

    // int i;
    public void visit(IntegerLiteral n)
    {
        emit("li $v0, " + n.i + "         # load literal " + n.i + " into $v0");
    }

    public void visit(Print n)
    {
        for (Exp exp : n.e)
        {
            exp.accept(this);
            emitComment("We must save the value of $a0 before print and restore it after");
            emit("move $t0, $a0     # Save $a0 into $t0");
            emit("move $a0,$v0      # Move value to $a0");
            emit("li $v0, 1         # Load system_call code 1, print_int");
            emit("syscall");
            emitComment("Restore $a0 from $t0");
            emit("move $a0, $t0 ");
        }

    }

    public void visit(PrintLn n)
    {
        for (Exp exp : n.list)
        {
            exp.accept(this);
            emitComment("We must save the value of $a0 before print and restore it after");
            emit("move $t0, $a0     # Save $a0 into $t0");            
            emit("move $a0,$v0      # Move value to $a0");
            emit("li $v0, 1         # Load system_call 1, print_int");
            emit("syscall");

            emit("li $v0, 4         # Load system_call 4, print_string");
            emit("la $a0, space     # Load address of newline label");
            emit("syscall");
            emitComment("Restore $a0 from $t0");
            emit("move $a0, $t0");            
        }
        emitComment("We must save the value of $a0 before print and restore it after");
        emit("move $t0, $a0     # Save $a0 into $t0");
        emit("li $v0, 4           # Load system_call 4, print_string");
        emit("la $a0, newline     # Load address of newline label");
        emit("syscall");
        emitComment("Restore $a0 from $t0");
        emit("move $a0, $t0");
    }

    public void visit(Plus n)
    {
        n.e1.accept(this);
        emit("subu $sp, 4       # Push $v0 onto stack");
        emit("sw $v0, ($sp)");
        n.e2.accept(this);
        emit("lw $t0, ($sp)     # Load stack contents into $v1");
        emit("addi $sp, 4       # Pop stack");
        emit("add $v0, $t0, $v0 # Add results and store value in $v0");
    }

    public void visit(Minus n)
    {
        n.e1.accept(this);
        emit("subu $sp, 4       # Push $v0 onto stack");
        emit("sw $v0, ($sp)");
        n.e2.accept(this);
        emit("lw $t0, ($sp)     # Load stack contents into $v1");
        emit("addi $sp, 4       # Pop stack");
        emit("sub $v0, $t0, $v0 # Subtract results and store value in $v0");
    }

    public void visit(Times n)
    {
        n.e1.accept(this);
        emit("subu $sp, 4       # Push $v0 onto stack");
        emit("sw $v0, ($sp)");
        n.e2.accept(this);
        emit("lw $t0, ($sp)     # Load stack contents into $v1");
        emit("addi $sp, 4       # Pop stack");
        emit("mul $v0, $t0, $v0 # Multiply results and store value in $v0");
    }

    public void visit(True n)
    {
        emit("li $v0, 1         # Load true in $v0");
    }

    public void visit(False n)
    {
        emit("li $v0, 0         # Load false in $v0");
    }

    private int globalIfCounter;

    public void visit(If n)
    {
        int localIfCounter = globalIfCounter++;
        n.e.accept(this);
        emit("beq $v0, $0,iffalse_" + localIfCounter
                + " # Jump to false label if condition is false");
        n.s1.accept(this);
        emit("j isdone_" + localIfCounter + "      # Jump to done label");

        emitLabel("iffalse_" + localIfCounter);
        n.s2.accept(this);
        emitLabel("isdone_" + localIfCounter);
    }

    private int globalAndCounter;

    public void visit(And n)
    {
        int localAndCounter = globalAndCounter++;
        n.e1.accept(this);
        emit("beq $v0, $0, andfalse_" + localAndCounter
                + "   # If left expression false, jump to andfalse for short circuit");
        n.e2.accept(this);// Nothing else required. Expression 2 will leave 1 in
                          // $v0 if it's true, or 0 if it is false.
        emitLabel("andfalse_" + localAndCounter);
    }

    private int globalOrCounter;

    public void visit(Or n)
    {
        int localOrCounter = globalOrCounter++;
        n.e1.accept(this);
        emit("li $t0, 1      # Load true into temporary for comparison");
        emit("beq $v0, $t0, ortrue_" + localOrCounter
                + "   # If left expression true, jump to ortrue_ for short circuit");
        n.e2.accept(this);// Nothing else required. Expression 2 will leave 1 in
                          // $v0 if it's true, or 0 if it is false.
        emitLabel("ortrue_" + localOrCounter);
    }

    private int globalNotCounter;

    public void visit(Not n)
    {
        int localNotCounter = globalNotCounter++;
        n.e.accept(this);
        emit("beq $v0, $0, notfalse_" + localNotCounter
                + "   # if the expression is false, jump to notfalse");
        emit("li $v0, 0           # in this case, not was 1. We replace $v0 with false");
        emit("j notdone_" + localNotCounter
                + "   # We are done flipping the flag, jump to the end label");
        emitLabel("notfalse_" + localNotCounter);
        emit("li $v0, 1    # Load 1 into $v0 to invert the false to true");
        emitLabel("notdone_" + localNotCounter);

    }

    private int globalEqCounter;

    public void visit(Equality n)
    {
        int localEqCounter = globalEqCounter++;
        n.e1.accept(this);
        emit("subu $sp, 4       # push stack");
        emit("sw $v0, ($sp)     # Save $v0 on stack");
        n.e2.accept(this);
        emit("lw $t0, ($sp)     # Restore left value from stack int $t0");
        emit("addi $sp, 4       # Pop stack");
        emit("beq $t0, $v0, eq_isequal_" + localEqCounter);
        emit("li $v0, 0         # Values are not equal, load false to $v0");
        emit("j eqdone_" + localEqCounter);
        emitLabel("eq_isequal_" + localEqCounter);
        emit("li $v0, 1         # Values are equal, load true to $v0");
        emitLabel("eqdone_" + localEqCounter);
    }

    private int globalLtCounter;

    public void visit(LessThan n)
    {
        int localLtCounter = globalLtCounter++;
        n.e1.accept(this);
        emit("subu $sp, 4       # push stack");
        emit("sw $v0, ($sp)     # Save $v0 on stack");
        n.e2.accept(this);
        emit("lw $t0, ($sp)     # Restore left value from stack int $t0");
        emit("addi $sp, 4       # Pop stack");
        emit("blt $t0, $v0, lt_islt_" + localLtCounter);
        emit("li $v0, 0         # Value is not less than, load false to $v0");
        emit("j ltdone_" + localLtCounter);
        emitLabel("lt_islt_" + localLtCounter);
        emit("li $v0, 1         # Value is less than, load true to $v0");
        emitLabel("ltdone_" + localLtCounter);
    }

    private int globalLteCounter;

    public void visit(LessThanOrEqual n)
    {
        int localLteCounter = globalLteCounter++;
        n.e1.accept(this);
        emit("subu $sp, 4       # push stack");
        emit("sw $v0, ($sp)     # Save $v0 on stack");
        n.e2.accept(this);
        emit("lw $t0, ($sp)     # Restore left value from stack int $t0");
        emit("addi $sp, 4       # Pop stack");
        emit("ble $t0, $v0, lte_islte_" + localLteCounter);
        emit("li $v0, 0         # Values are not equal, load false to $v0");
        emit("j ltedone_" + localLteCounter);
        emitLabel("lte_islte_" + localLteCounter);
        emit("li $v0, 1         # Values are equal, load true to $v0");
        emitLabel("ltedone_" + localLteCounter);
    }

    public void visit(ClassDeclSimple n)
    {
        currClass = symTable.getClass(n.i.s);
        for (int i = 0; i < n.vl.size(); i++)
        {
            RamVariable var = currClass.getVar(n.vl.elementAt(i).i.s);
            var.setMemoryOffset(i * 4);
        }
        
        for (int i = 0; i < n.ml.size(); i++)
            n.ml.elementAt(i).accept(this);
        currClass = null;
    }

    public void visit(MethodDecl n)
    {
        String label = currClass.getId() + "." + n.i;
        emitLabel(label);

        // We must store the method label with the method object so we can use
        // it in Call
        currMethod = symTable.getMethod(n.i.s, currClass.getId());


        emitComment("Prologue of method -- " + n.i.s);
        emit("subu $sp, $sp, 32           # Push new stack frame of 32 bytes");
        emit("sw $ra, 8($sp)              # Save return address at 8 bytes from $sp");
        emit("sw $fp, 0($sp)              # Save old frame pointer at $sp");
        emit("addiu $fp, $sp, 28          # Current frame pointer to top of stack frame");

        int localStackSize = n.vl.size() * 4;
        emitComment("Allocating stack space for local variables");
        emit("subu $sp, $sp, " + localStackSize + "      # Push stack space for locals");

        // Assign an offset to $fp for each local variable
        for (int i = n.vl.size() - 1 ; i >= 0; i--)
        {
            RamVariable var = currMethod.getVar(n.vl.elementAt(i).i.s);
            var.setMemoryOffset(32 + i * 4);
        }

        for (int i = 0; i < n.sl.size(); i++)
        {
            n.sl.elementAt(i).accept(this);
        }
        
        n.e.accept(this); // Handle return expression

        emitComment("Epilogue of method call -- " + n.i.s);
        emit("lw $ra, -20($fp)            # Restore return address register");
        emit("lw $fp, -28($fp)            # Restore frame pointer");
        emit("addi $sp, " + (32 + localStackSize) + "      # Pop stack");
        emit("jr $ra                      # Jump to caller");
        currMethod = null;
    }

    public void visit(Call n)
    {          
        emitComment("Saving input parameters before call to " + n.i.s);        
        emit("sw $a3,    ($fp)         #Store parameter 4");
        emit("sw $a2,  -4($fp)         #Store parameter 3");
        emit("sw $a1,  -8($fp)         #Store parameter 2");
        emit("sw $a0, -12($fp)         #Store this");

        n.e.accept(this);
        emit("subu $sp, $sp, 4     # Push stack to save address of callee object");
        emit("sw $v0, ($sp)        # Save result of expression of call to stack");
          
        emitComment("Storing outgoing parameters for call to " + n.i.s);
        int stackUsed = 0;
        // Loop backwards, so stack is in the correct order
        for (int i = n.el.size() - 1; i >= 0; i--)
        {
            n.el.elementAt(i).accept(this);
            if (i < 3)
            {
                emit("move $a" + (i + 1) + ", $v0       # Move result of expression into $a" + (i + 1));
            }
            else
            {
                stackUsed += 4;
                emit("subu $sp, $sp, 4            # Push value onto the stack");
                emit("sw   $v0, ($sp)               # Store value of $v0 onto stack");
            }
        }
        String label = null;
        if(n.e instanceof This)
            label = currClass.getId();
        else if(n.e instanceof IdentifierExp)
        {
            IdentifierExp idExp = ((IdentifierExp)n.e);
            RamVariable callVar = currMethod.getVar(idExp.s);
            if(callVar == null)
                callVar = currMethod.getParam(idExp.s);
            if(callVar == null)
                callVar = currClass.getVar(idExp.s);

            label = ((IdentifierType)callVar.type()).s;
        }
        else if(n.e instanceof Call)
        {
            Call callCall = ((Call)n.e);
            label = ((IdentifierType)callCall.e.accept(getExpVisitor())).s;
        }
        else
            label = ((IdentifierType)n.e.accept(getExpVisitor())).s; 
            
        label += "." + n.i.s;        
        emit("lw $a0," + stackUsed + "($sp)      # load callee expression address from stack");
        
        emit("jal " + label + "   # Jump to method label");
        
        
        emit("addi $sp, $sp, " + (stackUsed + 4) + " #   Pop stack used for parameters"); // Extra four for callee object address

        emitComment("restoring input parameters after call to " + n.i.s);
        emit("lw $a3,    ($fp)         # Restore parameter 4");
        emit("lw $a2,  -4($fp)         # Restore parameter 3");
        emit("lw $a1,  -8($fp)         # Restore parameter 2");
        emit("lw $a0, -12($fp)         # Restore parameter 1");
    }
    
    public void visit(IdentifierExp n)
    {
        if (currMethod != null)
        {
            RamVariable var = currMethod.getVar(n.s);
            RamVariable classVar = currClass.getVar(n.s);
            if (var != null)
            {
                emit("lw $v0, -" + var.getMemoryOffset() + "($fp)      # Load local variable " + n.s
                        + " into $v0");
            }
            else if (classVar != null)
            {
                emit("lw $v0, " + classVar.getMemoryOffset() + "($a0)      # Load instance variable " + n.s
                        + " into $v0");
                
            }
            else
            {
                var = currMethod.getParam(n.s);
                int paramIndex = currMethod.getParamIndex(n.s);
                if (paramIndex < 3)
                    emit("move $v0, $a" + (paramIndex + 1) + "     # Move parameter from register to $v0");
                else
                    emit("lw $v0, " + (4 + (paramIndex - 3) * 4)
                            + "($fp)        # Loading parameter from stack");
            }
        }
    }

    public void visit(Identifier n)
    {
        if (currMethod != null)
        {
            RamVariable var = currMethod.getVar(n.s);
            RamVariable classVar = currClass.getVar(n.s);
            if (var != null)
            {
                emit("subu $t0, $fp, " + var.getMemoryOffset() + "   # Load address of local variable " + n.s + " into $t0");
            }            
            else if (classVar != null)
            {
                var = currClass.getVar(n.s);
                emit("add $t0, $a0, " + var.getMemoryOffset() + "    # Load address of instance variable " + n.s + " into $t0");
                
            }
            else // It is a parameter
            {
                var = currMethod.getParam(n.s);
                int paramIndex = currMethod.getParamIndex(n.s);
                if (paramIndex > 2)  // We only handle stack parameters here. Caller must handle register params, since they have no address              
                    emit("add $t0, $fp, " + (4 + (paramIndex - 3) * 4) + "  # Load address of parameter from stack");
            }
        }

    }
    
    public void visit(Assign n)
    {
        n.e.accept(this);        
        RamVariable var = currMethod.getVar(n.i.s);        
        if(var == null)
        {
            var = currClass.getVar(n.i.s);
        }
        int paramIndex = currMethod.getParamIndex(n.i.s);
        
        
        if(var != null || paramIndex > 2)
        {            
            n.i.accept(this);            
            emit("sw $v0, ($t0)         # Store results of assignment into the address of $t0");
        }
        else if (paramIndex >= 0) // We are assigning to a register parameter
        {
            int regIndex = paramIndex + 1;
            emit("move $v0, $a" + regIndex + "       # Assign $v0 to param register $a"+ regIndex );
        }     

    }
    
    public void visit(PlusEquals n)
    {
        n.e.accept(this);        
        RamVariable var = currMethod.getVar(n.id.s);        
        if(var == null)
        {
            var = currClass.getVar(n.id.s);
        }
        int paramIndex = currMethod.getParamIndex(n.id.s);
        
        
        if(var != null || paramIndex > 2) 
        {            
            n.id.accept(this);          
            emit("lw $t1, ($t0)         # Load contents of address $t0 into $t1");
            emit("add $v0, $v0, $t1     # Add the results of the rhs with contents of the address $t0 and store in $t1");
            emit("sw $v0, ($t0)         # Store results of assignment into the address of $t0");
        }
        else if (paramIndex >= 0) // We are assigning to a register parameter
        {
            String regString = "$a" + (paramIndex + 1);
            emit("add " + regString + ",$v0, " + regString + "       # Add $v0 to param register " + regString );
        }    
        
    }
    
    int globalWhileCounter;
    public void visit(While n)
    {
        int localWhileCounter = globalWhileCounter++;
        emitComment("Beginning of while loop");
        emitLabel("while_start_" + localWhileCounter);
        n.e.accept(this);        
        emit("beq $v0, $0, while_end_" + localWhileCounter);        
        n.s.accept(this);
        emit("j while_start_" + localWhileCounter + "   # Jump to while test condition");
        emitLabel("while_end_" + localWhileCounter);
        
    }
    
    private void emitAllocateHeap(int size)
    {
        emit("move $t0, $a0     # Save $a0");
        emit("li $v0, 9     # load system_call 9 ");
        emit("li $a0, " + size + "    # size of heap to be allocated");
        emit("syscall");        
    }
    
    public void visit(NewObject n)
    {
        RamClass newClass = symTable.getClass(n.i.s);
        int objectSize = newClass.getVarCount() * 4;
        
       // if(objectSize == 0)
       //     objectSize = 4;
        
        emitComment("Allocate new object");
        emitAllocateHeap(objectSize);   
        emit("move $a0, $t0    # Restore $a0");
    }
    
    public void visit(This n)
    {
        emit("move $v0, $a0    # Move address of this object into $v0");        
    }
}
