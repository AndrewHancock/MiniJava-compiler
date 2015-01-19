package ir;

import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.List;

public class Frame extends Declaration
{
	private List<Identifier> parameters;
	private List<Identifier> locals;
	private BasicBlock startBlock = new BasicBlock();
	
	private TempAllocator allocator = new TempAllocator();	
	
		
	public Frame(String namespace, String id, int paramSize, int localSize, BasicBlock startBlock)
	{
		super(namespace, id);
		
		locals = new ArrayList<Identifier>(localSize);
		parameters = new ArrayList<Identifier>(paramSize);		
		this.startBlock = startBlock;
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
