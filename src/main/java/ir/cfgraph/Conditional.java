package ir.cfgraph;

import ir.visitor.IrVisitor;

import java.util.Collection;

public class Conditional implements Block
{	
	private BasicBlock conditionBlock;
	private BasicBlock trueBlock;
	private BasicBlock falseBlock;	
	
	public Conditional(BasicBlock parent)
	{
		parent.setSuccessor(this);
		conditionBlock = new BasicBlock(parent, new BasicBlock());
		this.trueBlock = new BasicBlock(parent, conditionBlock.getSuccessor());
		this.falseBlock = new BasicBlock(parent, conditionBlock.getSuccessor());		
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
		return conditionBlock.getParent();
	}

	public Block getSuccessor()
	{
		return conditionBlock.getSuccessor();
	}
	
	public BasicBlock getConditionBlock()
	{
		return conditionBlock;
	}

	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
		
	}
}
