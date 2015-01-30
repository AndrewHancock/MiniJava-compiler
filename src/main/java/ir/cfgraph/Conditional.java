package ir.cfgraph;

import ir.ops.Expression;
import ir.ops.RelationalOp;
import ir.ops.Statement;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.Collection;

public class Conditional implements Block
{	
	private Collection<Block> parents = new ArrayList<Block>();
	private Block successor;
	private RelationalOp test;
	private BasicBlock trueBlock;
	private BasicBlock falseBlock;	
	
	public Conditional(Block parent, RelationalOp test)
	{
		parents.add(parent);		
		parent.setSuccessor(this);
		successor = new BasicBlock();
		this.trueBlock = new BasicBlock(parent);
		this.falseBlock = new BasicBlock(parent);
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

	@Override
	public Block getSuccessor()
	{
		return successor;
	}
	
	@Override
	public void setSuccessor(Block successor)
	{
		this.successor = successor;
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
