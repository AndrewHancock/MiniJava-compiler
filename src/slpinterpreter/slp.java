package slpinterpreter;

interface Stm
{
    // An IdNumMap is passed, pre-constructed, to the top of the call stack.
    // This is so evaluating separate Stmt trees in parallel will maintain a separate
    // "namespace." 
    void evaluate(IdNumMap idMap);
    int maxargs();
}

class CompoundStm implements Stm
{
    Stm stm1, stm2;

    CompoundStm(Stm s1, Stm s2)
    {
        stm1 = s1;
        stm2 = s2;
    }

    public void evaluate(IdNumMap idMap)
    {
        stm1.evaluate(idMap);
        stm2.evaluate(idMap);
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
    String id;
    Exp exp;

    AssignStm(String i, Exp e)
    {
        id = i;
        exp = e;
    }

    public void evaluate(IdNumMap idMap)
    {
        idMap.update(id, exp.evaluate(idMap));
    }
    
    public int maxargs()
    {
        return exp.maxargs();
    }
}

class PrintStm implements Stm
{
    //This is a possibly misguided attempt to use closures....And generics!
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

    PrintStm(ExpList e)
    {
        exps = e;
    }

    public void evaluate(IdNumMap idMap)
    {   
        // Required for closure purposes
        final IdNumMap map = idMap;
        
        //We don't use result, so Object
        iterate(new Action<Object>()
        {
            boolean first = true;
            public void execute(ExpList current)
            {
                //Only prepend on a space starting with the second printed expression
                if(!first)
                    output.print(" ");
                output.print(Integer.toString(current.getExp().evaluate(map)));
                first = false; 
            }
        });
        output.printNewLine();
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
    int evaluate(IdNumMap idMap);
    int maxargs();
}

class IdExp implements Exp
{
    String id;

    IdExp(String i)
    {
        id = i;
    }

    public int evaluate(IdNumMap idMap)
    {
        return idMap.lookup(id);
    }
    
    public int maxargs()
    {
        return 0;
    }
}

class NumExp implements Exp
{
    int num;

    NumExp(int n)
    {
        num = n;
    }

    public int evaluate(IdNumMap idMap)
    {
        return num;
    }
    
    public int maxargs()
    {
        return 0;
    }
}

class OpExp implements Exp
{
    Exp left, right;
    int oper;
    final static int Plus = 1, Minus = 2, Times = 3, Div = 4;

    OpExp(Exp l, int o, Exp r)
    {
        left = l;
        oper = o;
        right = r;
    }

    public int evaluate(IdNumMap idMap)
    {
        switch (oper)
        {
            case Plus:
                return left.evaluate(idMap) + right.evaluate(idMap);
            case Minus:
                return left.evaluate(idMap) - right.evaluate(idMap);
            case Times:
                return left.evaluate(idMap) * right.evaluate(idMap);
            case Div:
                return left.evaluate(idMap) / right.evaluate(idMap);
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
    Stm stm;
    Exp exp;

    EseqExp(Stm s, Exp e)
    {
        stm = s;
        exp = e;
    }

    public int evaluate(IdNumMap idMap)
    {
        // Side effect only
        stm.evaluate(idMap);

        //This is the value we care about
        return exp.evaluate(idMap);
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
