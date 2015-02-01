package ir.cfgraph;

import ir.ops.Statement;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicBlock implements Block
{
	protected static int nextId;
	private int id;
	private Collection<Block> parents = new ArrayList<Block>();
	protected Block successor;	
	private List<CodePoint> codePoints = new ArrayList<CodePoint>();
	
	
	public BasicBlock()
	{
		id = nextId++;
	}
	
	public BasicBlock(Block parent)
	{
		this();		
	}	
	
	public void addStatement(Statement op)
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
	
	@Override
	public Block getSuccessor()
	{
		return successor;
	}
	
	public void setSuccessor(Block successor)
	{
		this.successor = successor;
	}
	
	public Collection<Block> getParents()
	{
		return parents;		
	}
	
	public void addParent(Block parent)
	{
		parents.add(parent);
	}

	@Override
	public int getId()
	{
		return id;
	}
}
