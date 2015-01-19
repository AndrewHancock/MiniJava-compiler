package ir;

import ir.visitor.IrVisitor;

public class RecordAccess implements Value
{
	private String namespace;
	private String id;
	private Value value;
	private int index;
	

	public RecordAccess(String namespace, String id, Value value, int index)
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
	
	public Value getValue()
	{
		return value;
	}

	public int getIndex()
	{
		return index;
	}
	
	
	

}
