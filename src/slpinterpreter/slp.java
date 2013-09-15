package slpinterpreter;

interface Stm
{
    Table evaluate(Table table);
    int maxargs();
}

class CompoundStm implements Stm
{
    private Stm stm1, stm2;

    public CompoundStm(Stm s1, Stm s2)
    {
        stm1 = s1;
        stm2 = s2;
    }

    public Table evaluate(Table table)
    {
        Table firstTable = stm1.evaluate(table);
        return stm2.evaluate(firstTable);
    }
    
    public int maxargs()
    {
        // The pain of the computational complexity and stack usage of this is extreme.
        // But it will be decided! It's concise.
        return stm1.maxargs() >stm2.maxargs() ? stm1.maxargs() : stm2.maxargs();
    }
}

class AssignStm implements Stm
{   
    private String id;
    private Exp exp;

    AssignStm(String i, Exp e)
    {
        id = i;
        exp = e;
    }

    public Table evaluate(Table table)
    {
        IntAndTable expResult = exp.evaluate(table);
        
        // This is the only place we need to handle the special case of a "null" table reference.
        // Since we cannot call update yet, we simply create the first table entry.
        if(expResult.getTable() == null)
            return new Table(id, expResult.getValue(), null);        
        
        return expResult.getTable().update(id, expResult.getValue());                
    }
    
    public int maxargs()
    {
        return exp.maxargs();
    }
}

class PrintStm implements Stm
{
    // The action class is used to generalize the iteration of the ExpList.
    private abstract class Action<T>
    {
        abstract void execute(ExpList list);
        protected T getResult()
        {
            return null;
        }        
    }
    
    // ConsoleOutput used by default.
    // Can be assigned alternate Output implementations in unit tests
    protected static Output output = new ConsoleOutput();
    private ExpList exps;

    public PrintStm(ExpList e)
    {
        exps = e;
    }

    //Note: Table is declared final so it can be used in the closure.
    public Table evaluate(final Table table)
    {        
        Action<Table> iterateAction = 
                new Action<Table>()
                {
                    boolean first = true;
                    
                    // 0 is a dummy value here and will never be used.
                    // This variable must be IntAndTable to retain the state of each iteration.                    
                    IntAndTable currentTable = new IntAndTable(0, table);
                    public void execute(ExpList current)
                    {
                        //Only prepend on a space starting with the second printed expression
                        if(!first)
                            output.print(" ");
                        currentTable = current.getExp().evaluate(currentTable.getTable()); 
                        output.print(Integer.toString(currentTable.getValue()));
                        first = false; 
                    }
                    
                    public Table getResult()
                    {
                        //Print a new line after iteration complete
                        output.printNewLine();
                        return currentTable.getTable();
                    }
                };
        iterate(iterateAction);
        return iterateAction.getResult();
    }
    
    public int maxargs()
    {   
        Action<Integer> maxArgs =      
        new Action<Integer>()
        {
            private int largestArgs = 0;
            private int argCount = 0;
            public void execute(ExpList current)
            {                
                argCount++;
                
                //Special case: NumExp and IdExp just increment argCount and "continue"
                if((current.getExp() instanceof NumExp) || (current.getExp() instanceof IdExp))
                {                    
                    return;
                }
                int currentMax = current.getExp().maxargs();                
                largestArgs = currentMax > largestArgs ? currentMax : largestArgs;                
            }
            
            public Integer getResult()
            {
                //Return the largestArgs, or this PrintStm if it was largest
                return largestArgs > argCount ? largestArgs : argCount;
            }
        };
        iterate(maxArgs);
        return maxArgs.getResult();        
    }
    
    private void iterate(Action<?> action)
    {
        //There's always at least one
        ExpList current = exps;
        do
        {
            action.execute(current);
        }
        while((current = current.getNext()) != null);
    }
}

interface Exp
{
    IntAndTable evaluate(Table table);
    int maxargs();
}

class IdExp implements Exp
{
    private String id;

    public IdExp(String i)
    {
        id = i;
    }

    public IntAndTable evaluate(Table table)
    {
        // Lookup will throw a RunTime exception if the id->num mapping is not found.
        // This method may also throw a NullPointerException if table is null.
        // These cases only occur when evaluating an id for which an assignment was never interpreted.
        return new IntAndTable(table.lookup(id), table);
    }
    
    public int maxargs()
    {
        return 0;
    }
}

class NumExp implements Exp
{
    private int num;

    public NumExp(int n)
    {
        num = n;
    }

    public IntAndTable evaluate(Table table)
    {
        return new IntAndTable(num, table);
    }
    
    public int maxargs()
    {
        return 0;
    }
}

class OpExp implements Exp
{
    public final static int Plus = 1, Minus = 2, Times = 3, Div = 4;
    private Exp left, right;
    private int oper;    

    public OpExp(Exp l, int o, Exp r)
    {
        left = l;
        oper = o;
        right = r;
    }
    
    public IntAndTable evaluate(Table table)
    {
        IntAndTable leftResult = left.evaluate(table);
        IntAndTable rightResult = right.evaluate(leftResult.getTable());
        switch (oper)
        {
            case Plus:                
                return new IntAndTable(leftResult.getValue() + rightResult.getValue(), rightResult.getTable());                
            case Minus:
                return new IntAndTable(leftResult.getValue() - rightResult.getValue(), rightResult.getTable());
            case Times:
                return new IntAndTable(leftResult.getValue() * rightResult.getValue(), rightResult.getTable());
            case Div:
                return new IntAndTable(leftResult.getValue() / rightResult.getValue(), rightResult.getTable());
            default:
                throw new RuntimeException("Invalid operator!");
        }
    }
    
    public int maxargs()
    {
        return left.maxargs() > right.maxargs() ? left.maxargs() : right.maxargs();
    }
}

class EseqExp implements Exp
{
    private Stm stm;
    private Exp exp;

    public EseqExp(Stm s, Exp e)
    {
        stm = s;
        exp = e;
    }

    public IntAndTable evaluate(Table table)
    {
        // Side effect only
        Table sideEffectTable = stm.evaluate(table);

        //This is the value we care about
        return exp.evaluate(sideEffectTable);
    }
    
    public int maxargs()
    {
        return stm.maxargs() > exp.maxargs() ? stm.maxargs() : exp.maxargs();
    }
}

abstract class ExpList
{
    protected Exp head;
    public Exp getExp()
    {
        return head;
    }
    
    //returns null on last element of list
    public abstract ExpList getNext();    
}

class PairExpList extends ExpList
{    
    private ExpList tail;

    public PairExpList(Exp h, ExpList t)
    {
        head = h;
        tail = t;
    }
    
    public ExpList getNext()
    {
        return tail;        
    }
}

class LastExpList extends ExpList
{
    public LastExpList(Exp h)
    {
        head = h;
    }
    
    public ExpList getNext()
    {
        return null;
    }
}
