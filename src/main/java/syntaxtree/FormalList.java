package syntaxtree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FormalList implements Iterable<Formal>
{
	private List<Formal> list;

	public FormalList()
	{
		list = new ArrayList<Formal>();
	}

	public void add(Formal n)
	{
		list.add(n);
	}

	public Formal get(int i)
	{
		return list.get(i);
	}

	public List<Formal> getList()
	{
		return list;
	}

	public int size()
	{
		return list.size();
	}

	@Override
	public Iterator<Formal> iterator()
	{
		return list.iterator();
	}
}
