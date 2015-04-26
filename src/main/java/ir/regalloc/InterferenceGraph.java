package ir.regalloc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class InterferenceGraph
{
	private Map<String, Set<String>> nodes = new HashMap<String, Set<String>>();
	public Set<String> addOrGetNode(String label)
	{
		Set<String> node = nodes.get(label);
		if(node == null)			
		{
			node = new HashSet<String>();
			nodes.put(label, node);
		}
		return node;
	}
	
	public void removeNode(String label)
	{
		for(Entry<String, Set<String>> entry : nodes.entrySet())
		{
			if(entry.getKey() != label)
				entry.getValue().remove(label);
		}
		nodes.remove(label);		
	}
	
	public void addEdge(String srcLabel, String destLabel)
	{
		
		addOrGetNode(srcLabel).add(destLabel);
		addOrGetNode(destLabel).add(srcLabel);
	}
		
	public List<Entry<String, Set<String>>> getEntryList()
	{
		List<Entry<String, Set<String>>> entryList = new ArrayList<Entry<String, Set<String>>>();
		entryList.addAll(nodes.entrySet());
		return entryList;
	}
}
