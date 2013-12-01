package visitor;
import syntaxtree.*;
import symboltable.*;
import java.util.Enumeration;


public class CodeGenerator extends DepthFirstVisitor {

    private java.io.PrintStream out;
    private Table symTable;
    private RamClass currClass;
    private RamMethod currMethod;   
    
    public CodeGenerator(java.io.PrintStream o, Table st) {
        out = o; 
        symTable = st;
    }

    private void emit(String s) {
		out.println("\t" + s);
    }

    private void emitLabel(String l) {
		out.println(l + ":");
    }
    
    private void emitComment(String s) {
        out.println("\t" + "#" + s);
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
        for ( int i = 0; i < n.cl.size(); i++ ) {
            n.cl.elementAt(i).accept(this);
        }

    }
    
    // Identifier i1, i2;
    // Statement s;
    public void visit(MainClass n) {
        symTable.addClass(n.i1.toString());
        currClass = symTable.getClass(n.i1.toString());
        symTable.getClass(n.i1.s).addMethod("main", new IdentifierType("void"));
        currMethod = symTable.getClass(n.i1.toString()).getMethod("main");
        symTable.getMethod("main", 
                currClass.getId()).addParam(n.i2.toString(), new IdentifierType("String[]"));

        emitLabel("main");
        
        emitComment("begin prologue -- main");
        emit("subu $sp, $sp, 24    # stack frame is at least 24 bytes");
        emit("sw $fp, 4($sp)       # save caller's frame pointer");
        emit("sw $ra, 0($sp)       # save return address");
        
        emit("addi $fp, $sp, 20    # set up main's frame pointer");       
        emitComment("end prologue -- main");
        
        n.s.accept(this);
        
        emitComment("begin epilogue -- main");
        emit("lw $ra, 0($sp)       # restore return address");
        emit("lw $fp, 4($sp)       # restore caller's frame pointer");
        emit("addi $sp, $sp, 24    # pop the stack"); 
        emitComment("end epilogue -- main");
        emit("jr $ra");
        emit("\n");       // end programs with new line
        
        currMethod = null;
        
    }
    
    // int i;
    public void visit(IntegerLiteral n) {
        emit("li $v0, "+n.i+"         # load literal "+n.i+" into $v0");
    }
    
    public void visit(Print n)
    {
        for(Exp exp : n.e)
        {
            exp.accept(this);
            emit("move $a0,$v0      # Move value to $a0");            
            emit("li $v0, 1         # Load system_call code 1, print_int");
            emit("syscall");
        }
    }
    
    public void visit(PrintLn n)
    {
        for(Exp exp : n.list)
        {
            exp.accept(this);
            emit("move $a0,$v0      # Move value to $a0");            
            emit("li $v0, 1         # Load system_call code 1, print_int");
            emit("syscall");
            
            emit("li $v0, 4         # Load system_call 4, print_string");
            emit("la $a0, space     # Load address of newline label");
            emit("syscall");            
        }
        
        emit("li $v0, 4           # Load system_call 4, print_string");
        emit("la $a0, newline     # Load address of newline label");
        emit("syscall");
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
        emit("beq $v0, $0,iffalse_" + localIfCounter+ " # Jump to false label if condition is false");        
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
        emit("beq $v0, $0, andfalse_" + localAndCounter + "   # If left expression false, jump to andfalse for short circuit");
        n.e2.accept(this);// Nothing else required. Expression 2 will leave 1 in $v0 if it's true, or 0 if it is false.        
        emitLabel("andfalse_" + localAndCounter);
    }
    
    private int globalOrCounter;
    public void visit(Or n)
    {
        int localOrCounter = globalOrCounter++;
        n.e1.accept(this);
        emit("li $t0, 1      # Load true into temporary for comparison");
        emit("beq $v0, $t0, ortrue_" + localOrCounter + "   # If left expression true, jump to ortrue_ for short circuit");
        n.e2.accept(this);// Nothing else required. Expression 2 will leave 1 in $v0 if it's true, or 0 if it is false.        
        emitLabel("ortrue_" + localOrCounter);
    }
    
    private int globalNotCounter;
    public void visit(Not n)
    {
        int localNotCounter = globalNotCounter++;
        n.e.accept(this);        
        emit("beq $v0, $0, notfalse_" + localNotCounter + "   # if the expression is false, jump to notfalse");
        emit("li $v0, 0           # in this case, not was 1. We replace $v0 with false");
        emit("j notdone_" + localNotCounter + "   # We are done flipping the flag, jump to the end label");
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
        emit("li $v0, 0         # Values are not equal, load false to $v0");
        emit("j ltdone_" + localLtCounter);        
        emitLabel("lt_islt_" + localLtCounter);
        emit("li $v0, 1         # Values are equal, load true to $v0");
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
}
