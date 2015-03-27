package ir.backend;

public class Register extends Value
{	
	private int registerIndex;
	public Register(int registerIndex)
	{		
		this.registerIndex = registerIndex;
	}
	
	public int getRegisterIndex()
	{
		return registerIndex;
	}
}
