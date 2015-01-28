package ir.ops;

import ir.visitor.IrVisitor;

public class RecordAccess implements Expression
{
	private String namespace;
	private String id;
	private Expression value;
	private int index;
	

	public RecordAccess(String namespace, String id, Expression value, int index)
	{
		this.namespace = namespace;
		this.id = id;		
		this.index = index;
		this.value = value;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

	public String getNamespace()
	{
		return namespace;
	}
	
	public String getId()
	{
		return id;
	}
	
	public Expression getValue()
	{
		return value;
	}

	public int getIndex()
	{
		return index;
	}
	
	
	

}
