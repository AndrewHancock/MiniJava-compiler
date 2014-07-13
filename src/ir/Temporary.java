package ir;

import ir.visitor.IrVisitor;

public class Temporary implements Alias
{	
	private String id;
	public Temporary(String id) { this.id = id; }
	@Override
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
