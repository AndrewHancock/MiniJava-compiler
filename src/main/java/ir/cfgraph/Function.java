package ir.cfgraph;

import ir.TempAllocator;
import ir.ops.Declaration;
import ir.ops.Identifier;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.List;

public class Function extends Declaration
{
	private List<Identifier> parameters;
	private List<Identifier> locals;	
	private Block startBlock;
	
	private TempAllocator allocator = new TempAllocator();	
	
		
	public Function(String namespace, String id, int paramSize, int localSize)
	{
		super(namespace, id);
		
		locals = new ArrayList<Identifier>(localSize);
		parameters = new ArrayList<Identifier>(paramSize);
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
		return allocator.getTemporaries();
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
