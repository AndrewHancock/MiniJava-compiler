package ir.cfgraph;

import java.util.Collection;

public abstract class CodePoint
{
	private Collection<CodePoint> parents;
	
	public Collection<CodePoint> getParents()
	{
		return parents;
	}
}
