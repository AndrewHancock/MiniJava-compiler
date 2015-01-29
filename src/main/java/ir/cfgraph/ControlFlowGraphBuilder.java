package ir.cfgraph;

import java.util.Stack;

import ir.ops.RelationalOp;
import ir.ops.Statement;

public class ControlFlowGraphBuilder
{
	private BasicBlock startingBlock = new BasicBlock();
	private BasicBlock currentBlock = startingBlock;
	private Conditional currentConditional = null;
	
	private Stack<Conditional> condStack = new Stack<Conditional>();
	private Stack<BasicBlock> blockStack = new Stack<BasicBlock>();
	
	public void addStatement(Statement statement)
	{
		currentBlock.addStatement(statement);
	}
	
	public void addCondition(RelationalOp op)
	{
		if(currentConditional != null)
		{
			blockStack.push(currentBlock);
			condStack.push(currentConditional);
		}
		currentConditional = new Conditional(currentBlock, op);		
	}
	
	public void beginTrueBlock()
	{
		if(currentConditional == null)
			throw new RuntimeException("Conditonal is null.");
		currentBlock = currentConditional.getTrueBlock();
	}
	
	public void beginFalseBlock()
	{
		if(currentConditional == null)
			throw new RuntimeException("Conditional is null.");
		currentBlock = currentConditional.getFalseBlock();
	}
	
	public void endConditional()
	{		
		if(!condStack.empty())
		{
			currentBlock = blockStack.pop();
			currentConditional = condStack.pop();
			
		}
		else
		{
			currentBlock = (BasicBlock)currentConditional.getSuccessor();
			currentConditional = null;
		}
	}
	
	public void clear()
	{
		startingBlock = new BasicBlock();
		currentBlock = startingBlock;
		currentConditional = null;
		condStack.clear();
		blockStack.clear();
	}
	
	public BasicBlock getStartingBlock()
	{
		return startingBlock;
	}
}
