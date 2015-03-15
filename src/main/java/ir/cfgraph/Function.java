package ir.cfgraph;

import ir.ops.Declaration;
import ir.ops.Identifier;
import ir.ops.Statement;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.List;

public class Function extends Declaration 
{
	private List<Identifier> parameters = new ArrayList<Identifier>();
	private List<Identifier> locals = new ArrayList<Identifier>();
	private List<Identifier> temporaries = new ArrayList<Identifier>();
	private List<Statement> statements = new ArrayList<Statement>();
			
	public Function(String namespace, String id)
	{
		super(namespace, id);
		
		locals = new ArrayList<Identifier>();
		parameters = new ArrayList<Identifier>();
		temporaries = new ArrayList<Identifier>();		
	}
	
	
	public Identifier getParam(int index)
	{
		return parameters.get(1);
	}
	
	public void setParam(int index, Identifier param)
	{
		parameters.set(index, param);
	}
	
	public List<Identifier> getParams()
	{
		return parameters;
	}
	
	public List<Identifier> getLocals()
	{
		return locals;
	}
	
	public List<Identifier> getTemporaries()
	{
		return temporaries;
	}
	
	public Identifier getLocal(int index)
	{
		return locals.get(index);
	}
	
	public void setLocal(int index, Identifier local)
	{
		locals.add(index, local);
	}	
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}	
	
	public void addStatement(Statement s)
	{
		statements.add(s);
	}
	
	public List<Statement> getStatements()
	{
		return statements;
	}
}
