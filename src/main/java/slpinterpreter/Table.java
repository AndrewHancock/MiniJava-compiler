package slpinterpreter;

public class Table
{
    private String id;
    private int value;
    private Table tail;
    
    public Table(String id, int value, Table tail)
    {
        this.id = id;
        this.value = value;
        this.tail = tail;
    }
    
    public String getId()
    {
        return id;        
    }
    
    public int getValue()
    {
        return value;        
    }
    
    public Table getTail()
    {
        return tail;
    }
    
    
    public Table update(String id, int num)
    {
        return new Table(id, num, this);        
    }
    
    public int lookup(String id)
    {
        Table table = this;
        do
        {
            if(table.getId().equals(id))
                return table.getValue();
        }
        while((table = table.getTail()) != null);
        
        //Id not found. Undefined behavior. Throw an exception
        throw new RuntimeException("Assignment " + id + " not found.");
    }
   
}
