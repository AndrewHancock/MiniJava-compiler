package ir;

import ir.visitor.IrVisitor;

public abstract class Declaration
{
	private String namespace;
	private String id;
	
	public Declaration(String namespace, String id)
	{
		this.namespace = namespace;
		this.id = id;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public String getId()
	{
		return id;
	}
	
	public abstract void accept(IrVisitor visitor);
}
