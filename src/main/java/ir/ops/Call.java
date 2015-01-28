package ir.ops;

import ir.visitor.IrVisitor;

import java.util.List;

public class Call implements Expression
{	
	String id;
	List<Expression> parameters;	
	
	public Call(String id, List<Expression> parameters)
	{
		this.id = id;
		this.parameters = parameters;		
	}
	
	public String getId()
	{
		return id;
	}
	
	public List<Expression> getParameters()
	{
		return parameters;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

}
