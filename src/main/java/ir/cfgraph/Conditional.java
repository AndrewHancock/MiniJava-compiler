package ir.cfgraph;

import ir.ops.RelationalOp;
import ir.ops.Expression;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.Collection;

public class Conditional implements Block
{	
	private Collection<Block> parents = new ArrayList<Block>();
	private Block successor;
	private Expression test;
	private BasicBlock trueBlock;
	private BasicBlock falseBlock;	
	
	public Conditional(BasicBlock parent, Expression test)
	{
		parents.add(parent);
		parent.setSuccessor(this);
		successor = new BasicBlock();
		this.trueBlock = new BasicBlock(parent, successor);
		this.falseBlock = new BasicBlock(parent, successor);
		this.test = test;
	}
	
	public BasicBlock getTrueBlock()
	{
		return trueBlock;
	}
	
	public BasicBlock getFalseBlock()
	{
		return falseBlock;
	}

	public Collection<Block> getParents()
	{
		return parents;
	}

	public Block getSuccessor()
	{
		return successor;
	}
	
	public Expression getTest()
	{
		return test;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
		
	}
}
