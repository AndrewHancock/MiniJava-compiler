package ir;

import ir.visitor.IrVisitor;

public class Temporary implements Value
{	
	private String id;
	public Temporary(String id) { this.id = id; }
	
	public String getName()
	{
		return id;
	}
	@Override
	public void accept(IrVisitor visitor)
	{
		// TODO Auto-generated method stub
		
	}

}
