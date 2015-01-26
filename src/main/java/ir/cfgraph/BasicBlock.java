package ir.cfgraph;

import ir.ops.Operation;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicBlock implements Block
{
	private Collection<Block> parents = new ArrayList<Block>();
	protected Block successor;	
	private List<CodePoint> codePoints = new ArrayList<CodePoint>();
	
	public BasicBlock()
	{
		
	}
	
	public BasicBlock(Block parent)
	{
		parents.add(parent);
	}
	
	public BasicBlock(Block parent, Block successor)
	{
		parents.add(parent);
		this.successor = successor;
	}	
	
	public void addOperation(Operation op)
	{
		CodePoint point = new CodePoint(op);
		codePoints.add(point);		
	}
	
	public List<CodePoint> getCodePoints()
	{
		return codePoints;
	}
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
	
	public Block getSuccessor()
	{
		return successor;
	}
	
	public void setSuccessor(Block successor)
	{
		this.successor = successor;
	}
	
	public Collection<Block> getParent()
	{
		return parents;		
	}
	
	public void addParent(Block parent)
	{
		parents.add(parent);
	}
}
