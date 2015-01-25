package ir;

import ir.ops.IdentifierExp;

import java.util.ArrayList;
import java.util.List;

public class TempAllocator
{	
	private ArrayList<IdentifierExp> temps = new ArrayList<IdentifierExp>();
	
	int tempCounter = 0;
	private String getNewTempName()
	{
		return "t" + ++tempCounter;
	}
	
	public IdentifierExp GetTemporary()
	{
		IdentifierExp newTemp = new IdentifierExp(getNewTempName());
		temps.add(newTemp);
		return newTemp;
		
	}
	
	public int getTemporaryCount()
	{
		return temps.size();
	}
	
	public List<IdentifierExp> getTemporaries()
	{
		return temps;
	}
}
