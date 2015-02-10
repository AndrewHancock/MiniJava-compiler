package ir.cfgraph;

import ir.ops.Expression;
import ir.ops.Statement;
import ir.visitor.IrVisitor;

public class Loop implements Block, ControlFlow
{	
	private int id;
	private Expression testResult;
	private Block successor;
	private Block test = new BasicBlock();
	private Block body = new BasicBlock();
	private boolean loopEnded;
	
	private Block currentBlock;
	
	public Loop()
	{
		id = BasicBlock.nextId++;	
	}
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	public void addParent(Block parent)
	{
	}

	@Override
	public void setSuccessor(Block successor)
	{
		this.successor = successor;
	}

	@Override
	public Block getSuccessor()
	{
		return successor;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public void addStatement(Statement statement)
	{
		currentBlock.addStatement(statement);
	}
	
	public void setTestResult(Expression testResult)
	{
		this.testResult = testResult;
	}
	
	public Expression getTestResult()
	{
		return testResult;
	}
	
	public void beginTest()
	{		
		currentBlock = test;
	}
	
	
	public void beginBody()
	{
		currentBlock = body;
	}
	
	public void endLoop()
	{
		loopEnded = true;
	}
	
	public boolean getLoopEnded()
	{
		return loopEnded;
	}

	public Block getTest()
	{
		return test;
	}

	public Block getBody()
	{
		return body;
	}
	
	@Override
	public void addBlock(Block b)
	{
		if(loopEnded)
			throw new RuntimeException("Loop is already ended. Cannot add block");
		
		currentBlock.setSuccessor(b);
	}
	@Override
	public boolean isComplete()
	{
		return loopEnded;
	}
}
