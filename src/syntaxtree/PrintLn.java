package syntaxtree;

import visitor.TypeVisitor;
import visitor.Visitor;

public class PrintLn extends Statement
{
    public ExpList list;
    
    public PrintLn(ExpList list)
    {
        this.list = list;
    }

    @Override
    public void accept(Visitor v)
    {
        v.visit(this);
    }

    @Override
    public Type accept(TypeVisitor v)
    {
        return v.visit(this);
    }

}
