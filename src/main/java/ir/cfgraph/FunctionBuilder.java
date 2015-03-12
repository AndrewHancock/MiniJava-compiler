package ir.cfgraph;

import java.util.List;
import java.util.Stack;

import ir.TempAllocator;
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
	private TempAllocator tempAllocator = new TempAllocator();	

	private Stack<Block> controlFlowStack = new Stack<Block>();	

	public FunctionBuilder()
	{
		
	}
	
	public List<Identifier> getTemporaries()
	{
		return tempAllocator.getTemporaries();
	}
	
	public void addStatement(Statement statement)
	{
		if (startingBlock == null)
		{
			startingBlock = new BasicBlock();
			currentBlock = startingBlock;
		}
		else if (currentBlock instanceof ControlFlow
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
		else if (currentBlock instanceof ControlFlow
				&& !((ControlFlow) currentBlock).isComplete())
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

	public void endBranch()
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
		else if (currentBlock instanceof ControlFlow
				&& !((ControlFlow) currentBlock).isComplete())
		{			
			ControlFlow cf = (ControlFlow)currentBlock;
			controlFlowStack.push(currentBlock);
			if (cf.isComplete())
				currentBlock.setSuccessor(currentBranch);
			else
				cf.addBlock(currentLoop);
		}	
		else
			currentBlock.setSuccessor(currentLoop);
		
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
		currentLoop.endLoop();
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
		return tempAllocator.GetTemporary();
	}
}
