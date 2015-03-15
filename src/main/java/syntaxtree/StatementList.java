package syntaxtree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatementList implements Iterable<Statement>
{
	private List<Statement> list;

	public StatementList()
	{
		list = new ArrayList<Statement>();
	}

	public void add(Statement n)
	{
		list.add(n);
	}

	public Statement get(int i)
	{
		return list.get(i);
	}
	
	public List<Statement> getList()
	{
		return list;
	}

	public int size()
	{
		return list.size();
	}

	@Override
	public Iterator<Statement> iterator()
	{
		return list.iterator(); 
	}
}
