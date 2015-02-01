package ir.cfgraph;

import java.lang.Thread.State;
import java.util.Stack;

import ir.ops.Identifier;
import ir.ops.RelationalOp;
import ir.ops.Statement;

public class ControlFlowGraphBuilder implements TemporaryProvider
{
	private Block startingBlock;
	private Block currentBlock;
	private Conditional currentConditional = null;
	private TemporaryProvider tempProvider;
	
	private Stack<Conditional> condStack = new Stack<Conditional>();
	
	public ControlFlowGraphBuilder(TemporaryProvider tempProvider)
	{
		this.tempProvider = tempProvider;
	}
	
	public void setControlFlowGraphBuilder(TemporaryProvider tempProvider)
	{
		this.tempProvider = tempProvider;
	}
	
	public void addStatement(Statement statement)
	{
		if(startingBlock == null)
		{
			startingBlock = new BasicBlock();
			currentBlock = startingBlock;
		}
		if(currentBlock instanceof Conditional && ((Conditional)currentBlock).state == Conditional.State.COMPLETE)
		{
			BasicBlock successor = new BasicBlock();
			currentBlock.setSuccessor(successor);
			currentBlock = successor;
		}
		currentBlock.addStatement(statement);
	}
	
	public void addCondition(RelationalOp op)
	{
		
		if(currentConditional != null)
		{			
			condStack.push(currentConditional);
		}
		currentConditional = new Conditional(op);
		if(startingBlock == null)
		{
			currentBlock = startingBlock = currentConditional;			 
		}		
		if(currentBlock instanceof Conditional)
		{
			if(((Conditional)currentBlock).state == Conditional.State.COMPLETE)
				currentBlock.setSuccessor(currentConditional);
			else
				((Conditional)currentBlock).addBlock(currentConditional);
		}
		else currentBlock.setSuccessor(currentConditional);

		currentConditional.addParent(currentBlock);

		currentBlock = currentConditional;
	}
	
	public void beginTrueBlock()
	{
		if(currentConditional == null)
			throw new RuntimeException("Conditional is null.");
		 currentConditional.beginTrueBlock();		 
	}
	
	public void beginFalseBlock()
	{
		if(currentConditional == null)
			throw new RuntimeException("Conditional is null.");
		currentConditional.beginFalseBlock();		
	}
	
	public void endConditional()
	{		
		currentConditional.state = Conditional.State.COMPLETE;
		if(!condStack.empty())
		{			
			
			currentConditional = condStack.pop();
			currentBlock = currentConditional;			
		}
		else
			currentConditional = null;
	}
	
	public void clear()
	{
		startingBlock = null;
		currentBlock = null;
		currentConditional = null;
		condStack.clear();		
	}
	
	public Block getStartingBlock()
	{
		return startingBlock;
	}
	
	@Override
	public Identifier getTemporary()
	{
		return tempProvider.getTemporary();
	}
}
