package ir.ops;

import ir.visitor.IrVisitor;

import java.util.List;

public class Call implements Value
{	
	String id;
	List<Value> parameters;	
	
	public Call(String id, List<Value> parameters)
	{
		this.id = id;
		this.parameters = parameters;		
	}
	
	public String getId()
	{
		return id;
	}
	
	public List<Value> getParameters()
	{
		return parameters;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

}
