package ir;

import ir.visitor.IrVisitor;

import java.util.List;

public class Call implements Operation
{	
	String id;
	List<Alias> parameters;
	Alias dest;
	
	public Call(String id, List<Alias> parameters, Alias dest)
	{
		this.id = id;
		this.parameters = parameters;
		this.dest = dest;
	}
	
	public String getId()
	{
		return id;
	}
	
	public List<Alias> getParameters()
	{
		return parameters;
	}
	
	public Alias getDest()
	{
		return dest;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

}
