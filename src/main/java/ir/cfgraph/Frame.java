package ir.cfgraph;

import ir.TempAllocator;
import ir.ops.Declaration;
import ir.ops.IdentifierExp;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.List;

public class Frame extends Declaration
{
	private List<IdentifierExp> parameters;
	private List<IdentifierExp> locals;	
	private Block startBlock = new BasicBlock();
	
	private TempAllocator allocator = new TempAllocator();	
	
		
	public Frame(String namespace, String id, int paramSize, int localSize, Block startBlock)
	{
		super(namespace, id);
		
		locals = new ArrayList<IdentifierExp>(localSize);
		parameters = new ArrayList<IdentifierExp>(paramSize);		
		this.startBlock = startBlock;
	}
	
	
	public IdentifierExp getParam(int index)
	{
		return parameters.get(1);
	}
	
	public void setParam(int index, IdentifierExp param)
	{
		parameters.set(index, param);
	}
	
	public List<IdentifierExp> getParams()
	{
		return parameters;
	}
	
	public List<IdentifierExp> getLocals()
	{
		return locals;
	}
	
	public List<IdentifierExp> getTemporaries()
	{
		return allocator.getTemporaries();
	}
	
	public IdentifierExp getLocal(int index)
	{
		return locals.get(index);
	}
	
	public void setLocal(int index, IdentifierExp local)
	{
		locals.add(index, local);
	}
	
	public TempAllocator getTempAllocator()
	{
		return allocator;
	}
	
	public Block getStartingBlock()
	{
		return startBlock;
	}
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}	

}
