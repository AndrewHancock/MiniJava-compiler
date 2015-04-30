package ir.backend;

public class Register extends Value
{
	public Register(String str)
	{
		super(str);		
	}
	
	@Override
	public String toString()
	{
		return "%" + super.toString();
	}

}
