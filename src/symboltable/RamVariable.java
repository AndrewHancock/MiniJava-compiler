package symboltable;

import syntaxtree.Type;

public class RamVariable
{
    private String id;
    private Type type;
    
    public RamVariable(String id, Type type)
    {
        this.id = id;
        this.type = type;
    }
    
    public String id()
    {
        return id;        
    }
    
    public Type type()
    {
        return type;
    }
    
    @Override
    public String toString()
    {
        return "class " + type + " " + id;
        
    }
 }
