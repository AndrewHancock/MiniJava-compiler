package visitor;

import syntaxtree.*;

public class ASTPrintVisitor implements Visitor
{

    // MainClass m;
    // ClassDeclList cl;
    public void visit(Program n)
    {
        System.out.println("Program(");
        n.m.accept(this);
        System.out.println("ClassDeclList(");
        for (int i = 0; i < n.cl.size(); i++)
        {
            if (i > 0)
                System.out.println(", ");
            n.cl.elementAt(i).accept(this);
        }
        System.out.println("))");
    }

    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n)
    {
        System.out.print("MainClass(");
        n.i1.accept(this);
        System.out.print(", ");
        n.i2.accept(this);
        System.out.print(", ");
        n.s.accept(this);
        System.out.println(")");
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n)
    {
        System.out.print("ClassDeclSimple(");
        n.i.accept(this);
        System.out.print(", (");
        for (int i = 0; i < n.vl.size(); i++)
        {
            n.vl.get(i).accept(this);
            if (i + 1 < n.vl.size())
                System.out.print(", ");
        }
        System.out.println("),");
        System.out.println("(");
        for (int i = 0; i < n.ml.size(); i++)
        {
            n.ml.elementAt(i).accept(this);
            if (i + 1 < n.ml.size())
                System.out.println(", ");
        }
        System.out.println("))");
    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n)
    {
        System.out.print("ClassDeclExtends(");
        n.i.accept(this);
        System.out.print(", ");
        n.j.accept(this);
        System.out.print(", (");
        for (int i = 0; i < n.vl.size(); i++)
        {
            n.vl.get(i).accept(this);
            if (i + 1 < n.vl.size())
                System.out.print(", ");
        }
        for (int i = 0; i < n.ml.size(); i++)
        {
            System.out.println();
            if (i + 1 < n.ml.size())
                System.out.println(", ");
        }
        System.out.println();
        System.out.println("))");
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n)
    {
        System.out.print("VarDecl(");
        n.t.accept(this);
        System.out.print(", ");
        n.i.accept(this);
        System.out.print(")");
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n)
    {
        System.out.print("MethodDecl(");
        n.t.accept(this);
        System.out.print(", ");
        n.i.accept(this);
        System.out.print(", (");
        for (int i = 0; i < n.fl.size(); i++)
        {
            n.fl.get(i).accept(this);
            if (i + 1 < n.fl.size())
                System.out.print(", ");
        }
        System.out.println("), (");
        for (int i = 0; i < n.vl.size(); i++)
        {
            n.vl.get(i).accept(this);
            if (i + 1 < n.vl.size())
                System.out.print(", ");
        }
        System.out.println("), (");
        for (int i = 0; i < n.sl.size(); i++)
        {
            n.sl.get(i).accept(this);
            if (i + 1 < n.sl.size())
                System.out.println(", ");
        }
        System.out.println("), ");
        n.e.accept(this);
        System.out.println(")");
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n)
    {
        System.out.print("Formal(");
        n.t.accept(this);
        System.out.print(", ");
        n.i.accept(this);
        System.out.print(")");
    }

    public void visit(IntArrayType n)
    {
        System.out.print("IntArrayType()");
    }

    public void visit(BooleanType n)
    {
        System.out.print("BooleanType()");
    }

    public void visit(IntegerType n)
    {
        System.out.print("IntegerType()");
    }

    // String s;
    public void visit(IdentifierType n)
    {
        System.out.print("IdentifierType(" + n.s + ")");
    }

    // StatementList sl;
    public void visit(Block n)
    {
        System.out.println("Block((");
        for (int i = 0; i < n.sl.size(); i++)
        {
            n.sl.get(i).accept(this);
            if (i + 1 < n.sl.size())
                System.out.println(",");
        }
        System.out.print("))");
    }

    // Exp e;
    // Statement s1,s2;
    public void visit(If n)
    {
        System.out.print("If(");
        n.e.accept(this);
        System.out.println(",");
        n.s1.accept(this);
        System.out.println(",");
        n.s2.accept(this);
        System.out.print(")");
    }

    // Exp e;
    // Statement s;
    public void visit(While n)
    {
        System.out.print("While(");
        n.e.accept(this);
        System.out.println(",");
        n.s.accept(this);
        System.out.print(")");
    }

    public void visit(ForEach n)
    {
        System.out.print("ForEach(");
        n.iterator.accept(this);
        System.out.print(",");
        n.source.accept(this);
        System.out.print(")");
    }

    // Exp e;
    public void visit(Print n)
    {
        System.out.print("Print(");
        n.e.elementAt(0).accept(this);
        for (int i = 1; i < n.e.size(); i++)
        {
            System.out.print(", ");
            n.e.elementAt(i).accept(this);
        }
        System.out.print(")");
    }

    // Exp e;
    public void visit(PrintLn n)
    {
        System.out.print("PrintLn(");
        n.list.elementAt(0).accept(this);
        for (int i = 1; i < n.list.size(); i++)
        {
            System.out.print(", ");
            n.list.elementAt(i).accept(this);
        }
        System.out.print(")");
    }

    // Identifier i;
    // Exp e;
    public void visit(Assign n)
    {
        System.out.print("Assign(");
        n.i.accept(this);
        System.out.print(", ");
        n.e.accept(this);
        System.out.print(")");
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n)
    {
        System.out.print("ArrayAssign(");
        n.i.accept(this);
        System.out.print(", ");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(And n)
    {
        System.out.print("And(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Or n)
    {
        System.out.print("Or(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Equality n)
    {
        System.out.print("Equality(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(LessThan n)
    {
        System.out.print("LessThan(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(LessThanOrEqual n)
    {
        System.out.print("LessThanOrEqual(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(Plus n)
    {
        System.out.print("Plus(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    public void visit(PlusEquals n)
    {
        System.out.print("PlusEquals(");
        n.id.accept(this);
        System.out.print(", ");
        n.e.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(Minus n)
    {
        System.out.print("Minus(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(Times n)
    {
        System.out.print("Times(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n)
    {
        System.out.print("ArrayLookup(");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e;
    public void visit(ArrayLength n)
    {
        System.out.print("ArrayLength(");
        n.e.accept(this);
        System.out.print(")");
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n)
    {
        System.out.print("Call(");
        n.e.accept(this);
        System.out.print(", ");
        n.i.accept(this);
        System.out.print(", (");
        for (int i = 0; i < n.el.size(); i++)
        {
            n.el.elementAt(i).accept(this);
            if (i + 1 < n.el.size())
            {
                System.out.print(", ");
            }
        }
        System.out.print("))");
    }

    // int i;
    public void visit(IntegerLiteral n)
    {
        System.out.print("IntegerLiteral(" + n.i + ")");
    }

    public void visit(True n)
    {
        System.out.print("True()");
    }

    public void visit(False n)
    {
        System.out.print("False()");
    }

    // String s;
    public void visit(IdentifierExp n)
    {
        System.out.print("IdentifierExp(" + n.s + ")");
    }

    public void visit(This n)
    {
        System.out.print("This()");
    }

    // Exp e;
    public void visit(NewArray n)
    {
        System.out.print("NewArray(");
        n.e.accept(this);
        System.out.print(")");
    }

    // Identifier i;
    public void visit(NewObject n)
    {
        System.out.print("NewObject(");
        System.out.print(n.i.s);
        System.out.print(")");
    }

    // Exp e;
    public void visit(Not n)
    {
        System.out.print("Not(");
        n.e.accept(this);
        System.out.print(")");
    }

    // String s;
    public void visit(Identifier n)
    {
        System.out.print("Identifier(" + n.s + ")");
    }
}
