package ir.ops;

import ir.visitor.IrVisitor;

public class RecordAccess implements Expression
{
	private String namespace;
	private String typeName;
	private Identifier identifier;
	private int recordIndex;	
	

	public RecordAccess(String namespace, String typeName, Identifier id, int recordIndex)
	{
		this.namespace = namespace;
		this.typeName = typeName;
		this.identifier = id;
		this.recordIndex = recordIndex;
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
	
	public Identifier getIdentifier()
	{
		return identifier;
	}
	
	public int getFieldIndex()
	{
		return recordIndex;
	}
	
	public String getTypeName()
	{
		return typeName;
	}

}
