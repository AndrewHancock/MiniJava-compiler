package ir.backend;

import java.util.HashMap;
import java.util.Map;

public class StackAllocator
{
	private int currentOffset;
	private Map<String, Value> locations = new HashMap<String, Value>();
	public void addIdentifier(String id)
	{
		locations.put(id, new StackOffset(currentOffset++));		
	}
	
	public Value getValueForIdentifier(String id)
	{
		return locations.get(id);
		
	}
	
	public int getStackSize()
	{
		return currentOffset + 8;
	}

}
