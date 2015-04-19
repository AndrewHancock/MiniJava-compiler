package syntaxtree.visitor;

import syntaxtree.Call;
import syntaxtree.Equality;
import syntaxtree.Exp;
import syntaxtree.ForEach;
import syntaxtree.IdentifierExp;
import syntaxtree.Assign;
import syntaxtree.IntegerType;
import syntaxtree.IdentifierType;
import syntaxtree.LessThanOrEqual;
import syntaxtree.Or;
import syntaxtree.Plus;
import syntaxtree.LessThan;
import syntaxtree.ClassDeclSimple;
import syntaxtree.If;
import syntaxtree.Not;
import syntaxtree.BooleanType;
import syntaxtree.Minus;
import syntaxtree.PlusEquals;
import syntaxtree.PrintLn;
import syntaxtree.Type;
import syntaxtree.True;
import syntaxtree.NewObject;
import syntaxtree.IntArrayType;
import syntaxtree.Program;
import syntaxtree.ArrayLength;
import syntaxtree.Identifier;
import syntaxtree.MainClass;
import syntaxtree.ClassDeclExtends;
import syntaxtree.While;
import syntaxtree.Print;
import syntaxtree.False;
import syntaxtree.IntegerLiteral;
import syntaxtree.Block;
import syntaxtree.MethodDecl;
import syntaxtree.Times;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayAssign;
import syntaxtree.NewArray;
import syntaxtree.And;
import syntaxtree.This;
import syntaxtree.VarDecl;
import syntaxtree.Formal;

public class TypeDepthFirstVisitor implements TypeVisitor
{

    // MainClass m;
    // ClassDeclList cl;
    public Type visit(Program n)
    {
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++)
        {
            n.cl.elementAt(i).accept(this);
        }
        return null;
    }

    // Identifier i1,i2;
    // Statement s;
    public Type visit(MainClass n)
    {
        n.i1.accept(this);
        n.i2.accept(this);
        n.s.accept(this);
        return null;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Type visit(ClassDeclSimple n)
    {
        n.i.accept(this);
        for (int i = 0; i < n.vl.size(); i++)
        {
            n.vl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++)
        {
            n.ml.elementAt(i).accept(this);
        }
        return null;
    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Type visit(ClassDeclExtends n)
    {
        n.i.accept(this);
        n.j.accept(this);
        for (int i = 0; i < n.vl.size(); i++)
        {
            n.vl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++)
        {
            n.ml.elementAt(i).accept(this);
        }
        return null;
    }

    // Type t;
    // Identifier i;
    public Type visit(VarDecl n)
    {
        n.t.accept(this);
        n.i.accept(this);
        return null;
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public Type visit(MethodDecl n)
    {
        n.t.accept(this);
        n.i.accept(this);
        for (int i = 0; i < n.fl.size(); i++)
        {
            n.fl.get(i).accept(this);
        }
        for (int i = 0; i < n.vl.size(); i++)
        {
            n.vl.get(i).accept(this);
        }
        for (int i = 0; i < n.sl.size(); i++)
        {
            n.sl.get(i).accept(this);
        }
        n.e.accept(this);
        return null;
    }

    // Type t;
    // Identifier i;
    public Type visit(Formal n)
    {
        n.t.accept(this);
        n.i.accept(this);
        return null;
    }

    public Type visit(IntArrayType n)
    {
        return null;
    }

    public Type visit(BooleanType n)
    {
        return null;
    }

    public Type visit(IntegerType n)
    {
        return null;
    }

    // String s;
    public Type visit(IdentifierType n)
    {
        return null;
    }

    // StatementList sl;
    public Type visit(Block n)
    {
        for (int i = 0; i < n.sl.size(); i++)
        {
            n.sl.get(i).accept(this);
        }
        return null;
    }

    // Exp e;
    // Statement s1,s2;
    public Type visit(If n)
    {
        n.e.accept(this);
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }

    // Exp e;
    // Statement s;
    public Type visit(While n)
    {
        n.e.accept(this);
        n.s.accept(this);
        return null;
    }

    // Exp e;
    public Type visit(Print n)
    {
        for(Exp exp : n.e)
            exp.accept(this);
        return null;
    }

    // Identifier i;
    // Exp e;
    public Type visit(Assign n)
    {
        n.i.accept(this);
        n.e.accept(this);
        return null;
    }

    // Identifier i;
    // Exp e1,e2;
    public Type visit(ArrayAssign n)
    {
        n.i.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Type visit(And n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Type visit(LessThan n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
    
    @Override
    public Type visit(LessThanOrEqual n)
    {
        
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
    
    public Type visit(Equality n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Type visit(Plus n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Type visit(Minus n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Type visit(Times n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Type visit(ArrayLookup n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e;
    public Type visit(ArrayLength n)
    {
        n.e.accept(this);
        return null;
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public Type visit(Call n)
    {
        n.e.accept(this);
        n.i.accept(this);
        for (int i = 0; i < n.el.size(); i++)
        {
            n.el.elementAt(i).accept(this);
        }
        return null;
    }

    // int i;
    public Type visit(IntegerLiteral n)
    {
        return null;
    }

    public Type visit(True n)
    {
        return null;
    }

    public Type visit(False n)
    {
        return null;
    }

    // String s;
    public Type visit(IdentifierExp n)
    {
        return null;
    }

    public Type visit(This n)
    {
        return null;
    }

    // Exp e;
    public Type visit(NewArray n)
    {
        n.e.accept(this);
        return null;
    }

    // Identifier i;
    public Type visit(NewObject n)
    {
        return null;
    }

    // Exp e;
    public Type visit(Not n)
    {
        n.e.accept(this);
        return null;
    }

    // String s;
    public Type visit(Identifier n)
    {
        return null;
    }

    @Override
    public Type visit(ForEach n)
    {        
        n.iterator.accept(this);
        n.source.accept(this);
        n.statement.accept(this);
        return null;
    }

    @Override
    public Type visit(PrintLn n)
    {
        for(Exp exp : n.list)
            exp.accept(this);            
        return null;
    }

    @Override
    public Type visit(Or n)
    {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    @Override
    public Type visit(PlusEquals n)
    {
        n.id.accept(this);
        n.e.accept(this);
        return null;
    }
}
