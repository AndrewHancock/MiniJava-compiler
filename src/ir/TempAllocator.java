package ir;

import java.util.Stack;

public class TempAllocator
{
	private Stack<Temporary> temps = new Stack<Temporary>();
	
	int tempCounter = 0;
	private String getNewTempName()
	{
		return "t" + ++tempCounter;
	}
	
	public Temporary GetTemporary()
	{
		if(! temps.empty())
			return temps.pop();
		else
			return new Temporary(getNewTempName());
	}
	
	public void returnTemporart(Temporary temp)
	{
		temps.push(temp);
	}
}
