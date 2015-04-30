package ir.backend;



public class RegisterDereference extends Register
{

	public RegisterDereference(String str)
	{
		super(str);
	}
	
	@Override
	public String toString()
	{
		return "(" + super.toString() + ")";
	}
}
