package ir;

import ir.visitor.IrVisitor;

public class RecordAccess implements Value
{
	private String namespace;
	private String id;
	

	public RecordAccess(String namespace, String id)
	{
		this.namespace = namespace;
		this.id = id;
		
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);		
	}

}
