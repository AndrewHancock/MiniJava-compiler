package syntaxtree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VarDeclList implements Iterable<VarDecl>
{
	private List<VarDecl> list;

	public VarDeclList()
	{
		list = new ArrayList<VarDecl>();
	}

	public void add(VarDecl n)
	{
		list.add(n);
	}

	public VarDecl get(int i)
	{
		return list.get(i);
	}

	public int size()
	{
		return list.size();
	}

	@Override
	public Iterator<VarDecl> iterator()
	{
		return list.iterator();
	}
}
