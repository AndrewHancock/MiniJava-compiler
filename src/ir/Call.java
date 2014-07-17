package ir;

import ir.visitor.IrVisitor;

import java.util.List;

public class Call implements Operation
{	
	String id;
	List<Value> parameters;
	Value dest;
	
	public Call(String id, List<Value> parameters, Value dest)
	{
		this.id = id;
		this.parameters = parameters;
		this.dest = dest;
	}
	
	public String getId()
	{
		return id;
	}
	
	public List<Value> getParameters()
	{
		return parameters;
	}
	
	public Value getDest()
	{
		return dest;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

}
