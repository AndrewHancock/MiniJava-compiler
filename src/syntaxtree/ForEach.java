package syntaxtree;

import visitor.TypeVisitor;
import visitor.Visitor;

public class ForEach extends Statement
{
    public Statement statement;
    public Type type;
    public Identifier iterator, source;
    
    public ForEach(Statement statement, Type type, Identifier iterator, Identifier source)
    {
        this.statement = statement;
        this.type = type;
        this.iterator = iterator;
        this.source = source;        
    }
    @Override
    public void accept(Visitor v)
    {
        v.visit(this);

    }

    @Override
    public Type accept(TypeVisitor v)
    {
        //return v.visit(this);
        return null;
    }

}
