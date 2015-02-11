package ir.cfgraph;

import ir.ops.Expression;
import ir.ops.RelationalOp;
import ir.ops.Statement;
import ir.visitor.IrVisitor;

public class Branch implements Block, ControlFlow
{	
	private int id;	
	private Block currentBlock;
	private Block successor;
	private RelationalOp test;
	private BasicBlock trueBlock;
	private BasicBlock falseBlock;
	private boolean isCompleted;
		
	public Branch(RelationalOp test)
	{
		id = BasicBlock.nextId++;		
		this.test = test;
	}
	
	public void beginTrueBlock()
	{
		// Will be null on first call
		currentBlock = trueBlock = new BasicBlock();		
	}
	
	public void beginFalseBlock()
	{	
		currentBlock = falseBlock = new BasicBlock();
	}	
	
	public void endBranch()
	{
		isCompleted = true;
	}
	
	public Block getTrueBlock()
	{
		return trueBlock;
	}
	
	public Block getFalseBlock()
	{
		return falseBlock;
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

	@Override
	public int getId()
	{		
		return id;
	}

	@Override
	public void addStatement(Statement statement)
	{
		if(currentBlock instanceof ControlFlow && ((ControlFlow)currentBlock).isComplete())
		{
			BasicBlock successor = new BasicBlock();
			currentBlock.setSuccessor(successor);
			currentBlock = successor;
		}	
			
		currentBlock.addStatement(statement);
	}
	
	@Override
	public void addBlock(Block block)
	{
		if(isCompleted)
			throw new RuntimeException("State of conditional is null.");		

		currentBlock.setSuccessor(block);
		currentBlock = block;
	}

	@Override
	public boolean isComplete()
	{
		return(isCompleted);
	}
}
