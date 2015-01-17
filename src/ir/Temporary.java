package ir;

import ir.visitor.IrVisitor;

public class Temporary implements Value
{	
	private String id;
	private int offset;
	public Temporary(String id, int offset) { this.id = id; this.offset = offset; }
	
	public String getId()
	{
		return id;
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
		
	}

}
