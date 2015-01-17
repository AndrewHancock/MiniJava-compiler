package ir;

import java.util.ArrayList;
import java.util.List;

public class TempAllocator
{	
	private ArrayList<Temporary> temps = new ArrayList<Temporary>();
	
	int tempCounter = 0;
	private String getNewTempName()
	{
		return "t" + ++tempCounter;
	}
	
	public Temporary GetTemporary()
	{
		Temporary newTemp = new Temporary(getNewTempName(), tempCounter);
		temps.add(newTemp);
		return newTemp;
		
	}
	
	public int getTemporaryCount()
	{
		return temps.size();
	}
	
	public List<Temporary> getTemporaries()
	{
		return temps;
	}
}
