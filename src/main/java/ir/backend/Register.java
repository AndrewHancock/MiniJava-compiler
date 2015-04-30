package ir.backend;

public class Register extends Value
{
	public Register(String str)
	{
		super(str);		
	}
	
	public Register(Register register)
	{
		super(register.str);
	}
	
	@Override
	public String toString()
	{
		return "%" + super.toString();
	}

}
