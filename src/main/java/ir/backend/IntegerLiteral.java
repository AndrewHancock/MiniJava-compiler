package ir.backend;


public class IntegerLiteral extends Value
{

	public IntegerLiteral(int i)
	{
		super(Integer.toString(i));
	}
	
	@Override
	public String toString()
	{
		return "$" + super.toString();
	}
}
