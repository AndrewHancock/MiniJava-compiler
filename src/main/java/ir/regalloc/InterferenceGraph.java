package ir.regalloc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class InterferenceGraph
{
	private Map<Integer, Set<Integer>> nodes = new HashMap<Integer, Set<Integer>>();
	public void addNode(int label)
	{
		nodes.put(label, new HashSet<Integer>());
	}
	
	public void removeNode(int label)
	{
		for(Entry<Integer, Set<Integer>> entry : nodes.entrySet())
		{
			if(entry.getKey() != label)
				entry.getValue().remove(label);
		}
		nodes.remove(label);		
	}
	
	public void addEdge(int srcLabel, int destLabel)
	{
		nodes.get(srcLabel).add(destLabel);
		nodes.get(destLabel).add(srcLabel);
	}
		
	public List<Entry<Integer, Set<Integer>>> getEntryList()
	{
		List<Entry<Integer, Set<Integer>>> entryList = new ArrayList<Entry<Integer, Set<Integer>>>();
		entryList.addAll(nodes.entrySet());
		return entryList;
	}
}
