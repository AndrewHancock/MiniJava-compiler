package ir.ops;

import ir.visitor.IrVisitor;

public class RecordAllocation implements Expression
{
	private String namespace;
	private String typeId;
	
	public RecordAllocation(String namespace, String typeId)
	{
		this.typeId = typeId;
		this.namespace = namespace;
	}
	
	@Override
	public void accept(IrVisitor v)
	{
		v.visit(this);
	}

	public String getNamespace()
	{
		return namespace;
	}

	public String getTypeId()
	{
		return typeId;
	}
}
