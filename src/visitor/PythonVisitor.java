package visitor;

import symboltable.RamClass;
import symboltable.RamMethod;
import symboltable.Table;
import syntaxtree.*;

public class PythonVisitor implements Visitor
{
    private StringBuilder out = new StringBuilder();
    private String programName;
    private RamClass currClass;
    private RamMethod currMethod;
    private Table symbolTable;
    
    public PythonVisitor(Table symbolTable)
    {
        this.symbolTable = symbolTable;
    }

    private void println(String s)
    {
        out.append(s);
        out.append("\n");
    }

    private void print(String s)
    {
        out.append(s);
    }

    private int level = 0;

    private void indent()
    {
        for (int i = 0; i < level; i++)
            out.append("    ");
    }

    @Override
    public void visit(Program n)
    {
        println("import sys");
        programName = n.m.i1.s;
        n.m.accept(this);

        for (int i = 0; i < n.cl.size(); i++)
        {
            n.cl.elementAt(i).accept(this);
        }
    }

    @Override
    public void visit(MainClass n)
    {
        println("def main() :");
        level++;
        indent();
        n.s.accept(this);
        level--;
        println("");

    }

    @Override
    public void visit(ClassDeclSimple n)
    {
        currClass = symbolTable.getClass(n.i.s);
        print("class ");
        n.i.accept(this);
        println(":");
        level++;
        
        for (int i = 0; i < n.ml.size(); i++)
        {
            n.ml.elementAt(i).accept(this);
        }
        level--;
        currClass = null;
    }

    @Override
    public void visit(ClassDeclExtends n)
    {
        // Not required for mini java

    }

    @Override
    public void visit(VarDecl n)
    {       
        if(!(n.t instanceof IntArrayType))
        {
            n.i.accept(this);
            n.t.accept(this);            
        }                
    }

    @Override
    public void visit(MethodDecl n)
    {
        currMethod = symbolTable.getMethod(n.i.s, currClass.getId());
        indent();
        print("def ");
        n.i.accept(this);
        print("(self");
        for (int i = 0; i < n.fl.size(); i++)
        {
            print(", ");
            n.fl.elementAt(i).accept(this);
        }
        println("):");
        level++;
        
        for (int i = 0; i < n.vl.size(); i++)
        {
            indent();
            n.vl.elementAt(i).accept(this);
            println("");
        }

        for (int i = 0; i < n.sl.size(); i++)
        {
            indent();
            n.sl.elementAt(i).accept(this);
            println("");
        }
        indent();
        print("return ");
        n.e.accept(this);
        println("");
        println("");
        level--;
        currMethod = null;
    }

    @Override
    public void visit(Formal n)
    {
        n.i.accept(this);
    }

    @Override
    public void visit(IntArrayType n)
    {
        //Handled in NewArray
    }

    @Override
    public void visit(BooleanType n)
    {        
        print(" = False");
    }

    @Override
    public void visit(IntegerType n)
    {
        print(" = 0");
    }

    @Override
    public void visit(IdentifierType n)
    {
        print(" = ");
        print(n.s);
        print("()");

    }

    @Override
    public void visit(Block n)
    {        
        for (int i = 0; i < n.sl.size(); i++)
        {            
            if(i > 0)
                indent();
            n.sl.elementAt(i).accept(this);
            println("");            
        }
    }

    @Override
    public void visit(If n)
    {
        print("if ");
        n.e.accept(this);
        println(":");
        level++;
        indent();
        n.s1.accept(this);
        level--;
        println("");
        indent();
        println("else:");
        level++;
        indent();
        n.s2.accept(this);
        level--;
    }

    @Override
    public void visit(While n)
    {
        print("while ");
        n.e.accept(this);
        println(":");
        level++;
        indent();
        n.s.accept(this);
        level--;
    }

    @Override
    public void visit(ForEach n)
    {
        print("for ");
        n.iterator.accept(this);
        print(" in ");
        n.source.accept(this);
        println(":");
        level++;
        indent();
        n.statement.accept(this);
        level--;
    }

    @Override
    public void visit(Print n)
    {
        print("sys.stdout.write(");
      
        for (int i = 0; i < n.e.size(); i++)
        {
            if (i > 0)
                print("+ ");
            print("str(");
            n.e.elementAt(i).accept(this);
            print(")");
        }
        println(")");
    }

    @Override
    public void visit(PrintLn n)
    {
        print("print ");

        for (int i = 0; i < n.list.size(); i++)
        {
            if (i > 0)
                print(", ");
            n.list.elementAt(i).accept(this);
        }
        println("");

    }

    @Override
    public void visit(Assign n)
    {
        if(currMethod.getVar(n.i.s) == null && currClass.getVar(n.i.s) != null)
            print("self.");
        n.i.accept(this);
        print(" = ");
        n.e.accept(this);
    }

    @Override
    public void visit(ArrayAssign n)
    {
        if(currMethod.getVar(n.i.s) == null && currClass.getVar(n.i.s) != null)
            print("self.");
        n.i.accept(this);
        print("[");
        n.e1.accept(this);
        print("] = ");
        n.e2.accept(this);
    }

    @Override
    public void visit(And n)
    {
        n.e1.accept(this);
        print(" and ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Or n)
    {
        n.e1.accept(this);
        print(" or ");
        n.e2.accept(this);

    }

    @Override
    public void visit(Equality n)
    {
        n.e1.accept(this);
        print(" == ");
        n.e2.accept(this);
    }

    @Override
    public void visit(LessThan n)
    {
        n.e1.accept(this);
        print(" < ");
        n.e2.accept(this);

    }

    @Override
    public void visit(LessThanOrEqual n)
    {
        n.e1.accept(this);
        print(" <= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Plus n)
    {
        n.e1.accept(this);
        print(" + ");
        n.e2.accept(this);

    }

    @Override
    public void visit(PlusEquals n)
    {
        if(currMethod.getVar(n.id.s) == null && currClass.getVar(n.id.s) != null)
            print("self.");
        n.id.accept(this);
        print(" += ");
        n.e.accept(this);
    }

    @Override
    public void visit(Minus n)
    {
        n.e1.accept(this);
        print(" - ");
        n.e2.accept(this);

    }

    @Override
    public void visit(Times n)
    {
        n.e1.accept(this);
        print(" * ");
        n.e2.accept(this);

    }

    @Override
    public void visit(ArrayLookup n)
    {
        n.e1.accept(this);
        print("[");
        n.e2.accept(this);
        print("]");
    }

    @Override
    public void visit(ArrayLength n)
    {        
        print("len(");
        n.e.accept(this);
        println(")");
    }

    @Override
    public void visit(Call n)
    {        
        n.e.accept(this);
        print(".");
        n.i.accept(this);
        print("(");
        for (int i = 0; i < n.el.size(); i++)
        {
            if (i > 0)
                print(", ");
            n.el.elementAt(i).accept(this);
        }
        print(")");

    }

    @Override
    public void visit(IntegerLiteral n)
    {
        print(new Integer(n.i).toString());
    }

    @Override
    public void visit(True n)
    {
        print("True");
    }

    @Override
    public void visit(False n)
    {
        print("False");
    }

    @Override
    public void visit(IdentifierExp n)
    {   
        if(currMethod.getVar(n.s) == null && currClass.getVar(n.s) != null)
            print("self.");
        print(n.s);
    }

    @Override
    public void visit(This n)
    {
        print("self");
    }

    @Override
    public void visit(NewArray n)
    {
        print("[None]*");
        n.e.accept(this);        
    }

    @Override
    public void visit(NewObject n)
    {
        n.i.accept(this);
        print("()");
    }

    @Override
    public void visit(Not n)
    {
        print("not ");
        n.e.accept(this);
    }

    @Override
    public void visit(Identifier n)
    {
        print(n.s);
    }

    @Override
    public String toString()
    {
        return out.toString();
    }
    
    public String getName()
    {
        return programName;
    }

}
