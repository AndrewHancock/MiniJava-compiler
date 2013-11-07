package visitor;

import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.Assign;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.Call;
import syntaxtree.ClassDeclExtends;
import syntaxtree.ClassDeclSimple;
import syntaxtree.Equality;
import syntaxtree.False;
import syntaxtree.ForEach;
import syntaxtree.Formal;
import syntaxtree.Identifier;
import syntaxtree.IdentifierExp;
import syntaxtree.IdentifierType;
import syntaxtree.If;
import syntaxtree.IntArrayType;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
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
import syntaxtree.VarDecl;
import syntaxtree.While;

public class PythonVisitor implements Visitor
{
    private StringBuilder out = new StringBuilder();

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
        print("class ");
        n.i.accept(this);
        println(":");
        level++;
        for (int i = 0; i < n.vl.size(); i++)
        {
            n.vl.elementAt(i).accept(this);
        }

        for (int i = 0; i < n.ml.size(); i++)
        {
            n.ml.elementAt(i).accept(this);
        }
        level--;
    }

    @Override
    public void visit(ClassDeclExtends n)
    {
        // Not required for mini java

    }

    @Override
    public void visit(VarDecl n)
    {       
        
    }

    @Override
    public void visit(MethodDecl n)
    {
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
        println("");

        level--;
    }

    @Override
    public void visit(Formal n)
    {
        // Unused

    }

    @Override
    public void visit(IntArrayType n)
    {
        // Unused

    }

    @Override
    public void visit(BooleanType n)
    {
        // Unused

    }

    @Override
    public void visit(IntegerType n)
    {
        // Unused

    }

    @Override
    public void visit(IdentifierType n)
    {
        // TODO: Handle class declaration?

    }

    @Override
    public void visit(Block n)
    {
        level++;
        for (int i = 0; i < n.sl.size(); i++)
        {
            indent();
            n.sl.elementAt(i).accept(this);
        }
        level--;

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
        n.s.accept(this);
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
        print("print ");

        for (int i = 0; i < n.e.size(); i++)
        {
            if (i > 0)
                print(", ");
            n.e.elementAt(i).accept(this);
        }
        println("");
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
        n.i.accept(this);
        print(" = ");
        n.e.accept(this);
    }

    @Override
    public void visit(ArrayAssign n)
    {
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
        n.e.accept(this);
        println(".length");
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
        print("true");

    }

    @Override
    public void visit(False n)
    {
        print("false");
    }

    @Override
    public void visit(IdentifierExp n)
    {
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
        print("[0 for x in range(");
        n.e.accept(this);
        print(")]");
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

}
