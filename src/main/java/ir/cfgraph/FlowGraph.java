package ir.cfgraph;

public class FlowGraph
{
	private CodePoint entry;
	private CodePoint exit;
	
	public FlowGraph(CodePoint entry, CodePoint exit)
	{
		this.entry = entry;
		this.exit = exit;
	}

	public CodePoint getEntry()
	{
		return entry;
	}

	public CodePoint getExit()
	{
		return exit;
	}
}
