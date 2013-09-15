package slpinterpreter;

public class IntAndTable
{
    private int value;
    private Table table;
    
    public IntAndTable(int value, Table table)
    {
        this.value = value;
        this.table = table;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public Table getTable()
    {
        return table;
    }
}
