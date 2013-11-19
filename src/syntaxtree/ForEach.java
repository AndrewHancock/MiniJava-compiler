package syntaxtree;

import visitor.TypeVisitor;
import visitor.Visitor;

public class ForEach extends Statement
{
    public Statement statement;
    public Type type;
    public IdentifierExp iterator;
    public IdentifierExp source;
    
    public ForEach(Statement statement, Type type, IdentifierExp iterator, IdentifierExp source)
    {
        this.type = type;
        this.statement = statement;        
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
        return v.visit(this);        
    }

}
