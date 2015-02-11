package ir.cfgraph;

import java.util.Stack;

import ir.ops.Expression;
import ir.ops.Identifier;
import ir.ops.RelationalOp;
import ir.ops.Statement;

public class FunctionBuilder implements TemporaryProvider
{
	private Block startingBlock;
	private Block currentBlock;
	private Branch currentBranch = null;
	private Loop currentLoop = null;
	private TemporaryProvider tempProvider;

	private Stack<Block> controlFlowStack = new Stack<Block>();	

	public FunctionBuilder(TemporaryProvider tempProvider)
	{
		this.tempProvider = tempProvider;
	}

	public void setControlFlowGraphBuilder(TemporaryProvider tempProvider)
	{
		this.tempProvider = tempProvider;
	}

	public void addStatement(Statement statement)
	{
		if (startingBlock == null)
		{
			startingBlock = new BasicBlock();
			currentBlock = startingBlock;
		}
		if (currentBlock instanceof ControlFlow
				&& ((ControlFlow) currentBlock).isComplete())
		{
			BasicBlock successor = new BasicBlock();
			currentBlock.setSuccessor(successor);
			currentBlock = successor;
		}
		currentBlock.addStatement(statement);
	}

	public void addBranch(RelationalOp op)
	{			
		currentBranch = new Branch(op);
		if (startingBlock == null)
		{
			currentBlock = startingBlock = currentBranch;
		}

		if (currentBlock instanceof ControlFlow)
		{			
			ControlFlow cf = (ControlFlow)currentBlock;
			controlFlowStack.push(currentBlock);
			if (cf.isComplete())
				currentBlock.setSuccessor(currentBranch);
			else
				cf.addBlock(currentBranch);
		}	
		else
			currentBlock.setSuccessor(currentBranch);

		currentBlock = currentBranch;
	}

	public void beginTrueBlock()
	{
		if (currentBranch == null)
			throw new RuntimeException("Conditional is null.");
		currentBranch.beginTrueBlock();
	}

	public void beginFalseBlock()
	{
		if (currentBranch == null)
			throw new RuntimeException("Conditional is null.");
		currentBranch.beginFalseBlock();
	}

	public void endConditional()
	{
		currentBranch.endBranch();
		if (!controlFlowStack.empty())
		{
			currentBlock = controlFlowStack.pop();
			if(currentBlock instanceof Branch)
				currentBranch = (Branch)currentBlock;
			else
				currentBranch = null;
			
			if(currentBlock instanceof Loop)
				currentLoop = (Loop)currentBlock;
			else
				currentLoop = null;
		}
		else
			currentBranch = null;
	}

	public void addLoop()
	{
		currentLoop = new Loop();
		if (startingBlock == null)
		{
			currentBlock = startingBlock = currentLoop;
		}

		if (currentBlock instanceof ControlFlow)
		{			
			ControlFlow cf = (ControlFlow)currentBlock;
			controlFlowStack.push(currentBlock);
			if (cf.isComplete())
				currentBlock.setSuccessor(currentBranch);
			else
				cf.addBlock(currentBranch);
		}	
		else
			currentBlock.setSuccessor(currentBranch);
		
		currentBlock = currentLoop;
	}

	public void setLoopTestResult(Expression testResult)
	{
		currentLoop.setTestResult(testResult);
	}

	public void beginTest()
	{
		currentLoop.beginTest();
	}

	public void beginBody()
	{
		currentLoop.beginBody();
	}

	public void endLoop()
	{		
		if (!controlFlowStack.empty())
		{
			currentBlock = controlFlowStack.pop();
			if(currentBlock instanceof Branch)
				currentBranch = (Branch)currentBlock;
			else
				currentBranch = null;
			
			if(currentBlock instanceof Loop)
				currentLoop = (Loop)currentBlock;
			else
				currentLoop = null;
		}
		else
			currentLoop = null;
	}

	public void clear()
	{
		startingBlock = null;
		currentBlock = null;
		currentBranch = null;
		controlFlowStack.clear();
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
