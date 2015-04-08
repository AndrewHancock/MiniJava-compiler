package ir.cfgraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

public abstract class CodePoint
{
	private Collection<CodePoint> parents = new ArrayList<CodePoint>();
	private BitSet liveness = new BitSet();
	
	public Collection<CodePoint> getParents()
	{
		return parents;
	}
	
	public void addParent(CodePoint parent)
	{
		parents.add(parent);
	}
	
	public abstract void accept(Visitor v);
	
	public BitSet getLiveness()
	{
		return liveness;
	}
}
