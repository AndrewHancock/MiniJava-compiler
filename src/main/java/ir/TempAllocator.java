package ir;

import ir.ops.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TempAllocator
{	
	private ArrayList<Identifier> temps = new ArrayList<Identifier>();
	
	int tempCounter = 0;
	private String getNewTempName()
	{
		return "t" + ++tempCounter;
	}
	
	public Identifier GetTemporary()
	{
		Identifier newTemp = new Identifier(getNewTempName());
		temps.add(newTemp);
		return newTemp;
		
	}
	
	public int getTemporaryCount()
	{
		return temps.size();
	}
	
	public List<Identifier> getTemporaries()
	{
		return temps;
	}
}
