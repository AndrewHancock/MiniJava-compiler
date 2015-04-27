package ir.cfgraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class CodePoint
{
	private Collection<CodePoint> parents = new ArrayList<CodePoint>();	
	
	public Collection<CodePoint> getParents()
	{
		return parents;
	}
	
	public void addParent(CodePoint parent)
	{
		parents.add(parent);
	}
	
	public abstract void accept(Visitor v);	
}
