package ir.cfgraph;

import ir.ops.Declaration;
import ir.ops.Identifier;
import ir.visitor.IrVisitor;
import java.util.ArrayList;
import java.util.List;

public class Function extends Declaration 
{
	private List<Identifier> parameters;
	private List<Identifier> locals;
	private List<Identifier> temporaries;
	private Block startBlock;
	
	
	
		
	public Function(String namespace, String id)
	{
		super(namespace, id);
		
		locals = new ArrayList<Identifier>();
		parameters = new ArrayList<Identifier>();
		temporaries = new ArrayList<Identifier>();		
	}
	
	
	public Identifier getParam(int index)
	{
		return parameters.get(1);
	}
	
	public void setParam(int index, Identifier param)
	{
		parameters.set(index, param);
	}
	
	public List<Identifier> getParams()
	{
		return parameters;
	}
	
	public List<Identifier> getLocals()
	{
		return locals;
	}
	
	public List<Identifier> getTemporaries()
	{
		return temporaries;
	}
	
	public Identifier getLocal(int index)
	{
		return locals.get(index);
	}
	
	public void setLocal(int index, Identifier local)
	{
		locals.add(index, local);
	}
	
	public Block getStartingBlock()
	{
		return startBlock;
	}
	
	public void setStartingBlock(Block startBlock)
	{
		this.startBlock = startBlock;
	}
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}	

}
