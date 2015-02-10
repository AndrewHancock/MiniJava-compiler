package ir.cfgraph;

import ir.ops.Expression;
import ir.ops.RelationalOp;
import ir.ops.Statement;
import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.Collection;

public class Conditional implements Block, ControlFlow
{	
	protected enum State
	{
		PENDING,
		TRUE,
		FALSE,
		COMPLETE
		
	}
	private int id;
	private Collection<Block> parents = new ArrayList<Block>();
	private Block currentBlock;
	private Block successor;
	private RelationalOp test;
	private BasicBlock trueBlock;
	private BasicBlock falseBlock;
	protected State state = State.PENDING;	
		
	public Conditional(RelationalOp test)
	{
		id = BasicBlock.nextId++;		
		this.test = test;
	}
	
	public void beginTrueBlock()
	{		
		state = State.TRUE;
		// Will be null on first call
		currentBlock = trueBlock = new BasicBlock();		
	}
	
	public void beginFalseBlock()
	{
		state = State.FALSE;
		currentBlock = falseBlock = new BasicBlock();
	}	
	
	public Block getTrueBlock()
	{
		return trueBlock;
	}
	
	public Block getFalseBlock()
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
	
	public void addParent(Block parent)
	{
		parents.add(parent);
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
		if(state == null || state == State.COMPLETE)
			throw new RuntimeException("Invalid Conditional state.");
		
		if(currentBlock == null)
		{
			if(state == State.TRUE)
			{
				trueBlock = new BasicBlock();
				currentBlock = trueBlock;
			}
			else if (state == State.FALSE)
			{
				falseBlock = new BasicBlock();
				currentBlock = falseBlock;
			}
		}	
			
		currentBlock.addStatement(statement);
	}
	
	@Override
	public void addBlock(Block block)
	{
		if(state == null)
			throw new RuntimeException("State of conditional is null.");
		
		if(currentBlock == null)
		{
			if(state == State.TRUE)
			{
				trueBlock = new BasicBlock();
				currentBlock = trueBlock;
			}
			else if (state == State.FALSE)
			{
				falseBlock = new BasicBlock();
				currentBlock = falseBlock;
			}			
		}
		else
			currentBlock.setSuccessor(block);
		currentBlock = block;		
	}

	@Override
	public boolean isComplete()
	{
		return(state == State.COMPLETE);
	}
}
