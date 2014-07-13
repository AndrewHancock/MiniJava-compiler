package ir;

import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Frame
{
	private String id;
	private List<Identifier> parameters;
	private List<Identifier> locals;
	private BasicBlock startBlock = new BasicBlock();
	
	private TempAllocator allocator = new TempAllocator();
	
		
	public Frame(String id, int paramSize, int localSize, BasicBlock startBlock)
	{
		this.id = id;	
		locals = new ArrayList<Identifier>(localSize);
		parameters = new ArrayList<Identifier>(paramSize);		
		this.startBlock = startBlock;
	}
	
	public String getId()
	{
		return id;
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
	
	public Identifier getLocal(int index)
	{
		return locals.get(index);
	}
	
	public void setLocal(int index, Identifier local)
	{
		locals.add(index, local);
	}
	
	public TempAllocator getTempAllocator()
	{
		return allocator;
	}
	
	public BasicBlock getStartingBlock()
	{
		return startBlock;
	}
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	

}
