package ir.backend;

import java.util.HashMap;
import java.util.Map;

public class RegisterAllocator
{	
	private int count;
	private int currentRegister;
	private int currentStackOffset;
	
	private Map<String, Value> locationMap = new HashMap<String, Value>();
	public RegisterAllocator(int registerCount)
	{
		this.count = registerCount;
	}
	
	public void addIdentifier(String id)
	{
		if(currentRegister < count - 1)
		{
			locationMap.put(id, new Register(currentRegister++));			
		}
		else
		{
			currentStackOffset += 8;
			locationMap.put(id, new StackOffset(currentStackOffset));
		}
	}	
	public Value getValueForIdentifier(String id)
	{
		return locationMap.get(id);
	}
	
	public void clear()
	{
		currentRegister = 0;
		currentStackOffset = 0;
		locationMap.clear();
	}
	
	public int getStackSize()
	{
		return currentStackOffset + 8;
	}
}
