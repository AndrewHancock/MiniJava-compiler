package syntaxtree;

import java.util.ArrayList;
import java.util.Iterator;

public class ExpList implements Iterable<Exp>
{
    private ArrayList<Exp> list;

    public ExpList()
    {
        list = new ArrayList<Exp>();
    }

    public void addElement(Exp n)
    {
        list.add(n);
    }

    public Exp elementAt(int i)
    {
        return list.get(i);
    }

    public int size()
    {
        return list.size();
    }

    @Override
    public Iterator<Exp> iterator()
    {

        return list.iterator();
    }
}
