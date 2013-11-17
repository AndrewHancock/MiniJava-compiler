package syntaxtree;

import visitor.TypeVisitor;
import visitor.Visitor;

public class ForEach extends Statement
{
    public Statement statement;
    public VarDecl iterator;
    public IdentifierType source;
    
    public ForEach(Statement statement, VarDecl iterator, IdentifierType source)
    {
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
