package ir.regalloc;

public class Register extends Value
{		
	public Register(int registerIndex)
	{		
		super(registerIndex);
	}
	
	public int getRegisterIndex()
	{
		return getValue();
	}
}
