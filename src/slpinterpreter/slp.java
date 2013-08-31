package slpinterpreter;

interface Stm
{
    // An IdNumMap is passed, pre-constructed, to the top of the call stack.
    // This is so evaluating separate Stmt trees in parallel will maintain a separate
    // "namespace." 
    void evaluate(IdNumMap idMap);
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
}

class PrintStm implements Stm
{
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
        
        boolean first = true;
        
        //There is always at least one
        ExpList current = exps;
        do
        {
            //Only prepend on a space starting with the second printed expression
            if(!first)
                output.print(" ");
            output.print(Integer.toString(current.getExp().evaluate(idMap)));
            first = false;
        }
        while((current = current.getNext()) != null);
        output.printNewLine();
    }
}

interface Exp
{
    int evaluate(IdNumMap idMap);
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
