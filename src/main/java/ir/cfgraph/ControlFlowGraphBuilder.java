package ir.cfgraph;

import java.lang.Thread.State;
import java.util.Stack;

import ir.ops.Expression;
import ir.ops.Identifier;
import ir.ops.RelationalOp;
import ir.ops.Statement;

public class ControlFlowGraphBuilder implements TemporaryProvider
{
	private Block startingBlock;
	private Block currentBlock;
	private Conditional currentConditional = null;
	private Loop currentLoop = null;
	private TemporaryProvider tempProvider;

	private Stack<Block> controlFlowStack = new Stack<Block>();
	private Stack<Loop> loopStack = new Stack<Loop>();

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
		if (startingBlock == null)
		{
			startingBlock = new BasicBlock();
			currentBlock = startingBlock;
		}
		if (currentBlock instanceof Conditional
				&& ((Conditional) currentBlock).state == Conditional.State.COMPLETE
				|| currentBlock instanceof Loop
				&& ((Loop) currentBlock).getLoopEnded())
		{
			BasicBlock successor = new BasicBlock();
			currentBlock.setSuccessor(successor);
			currentBlock = successor;
		}
		currentBlock.addStatement(statement);
	}

	public void addCondition(RelationalOp op)
	{			
		currentConditional = new Conditional(op);
		if (startingBlock == null)
		{
			currentBlock = startingBlock = currentConditional;
		}

		if (currentBlock instanceof ControlFlow)
		{			
			ControlFlow cf = (ControlFlow)currentBlock;
			controlFlowStack.push(currentBlock);
			if (cf.isComplete())
				currentBlock.setSuccessor(currentConditional);
			else
				cf.addBlock(currentConditional);
		}	
		else
			currentBlock.setSuccessor(currentConditional);

		currentBlock = currentConditional;
	}

	public void beginTrueBlock()
	{
		if (currentConditional == null)
			throw new RuntimeException("Conditional is null.");
		currentConditional.beginTrueBlock();
	}

	public void beginFalseBlock()
	{
		if (currentConditional == null)
			throw new RuntimeException("Conditional is null.");
		currentConditional.beginFalseBlock();
	}

	public void endConditional()
	{
		currentConditional.state = Conditional.State.COMPLETE;
		if (!controlFlowStack.empty())
		{
			currentBlock = controlFlowStack.pop();
			if(currentBlock instanceof Conditional)
				currentConditional = (Conditional)currentBlock;
			else
				currentConditional = null;
			
			if(currentBlock instanceof Loop)
				currentLoop = (Loop)currentBlock;
			else
				currentLoop = null;
		}
		else
			currentConditional = null;
	}

	public void addLoop()
	{
		if (currentLoop != null)
			loopStack.push(currentLoop);
		currentLoop = new Loop();
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
		if (!loopStack.isEmpty())
		{
			currentLoop = loopStack.pop();
			currentBlock = currentLoop;
		}
		else
			currentLoop = null;
	}

	public void clear()
	{
		startingBlock = null;
		currentBlock = null;
		currentConditional = null;
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
