package ir.cfgraph;

import ir.ops.ConditionalJump;

public class BranchCodePoint extends CodePoint
{	
	private ConditionalJump condition;
	private CodePoint taken;
	private CodePoint fallthrough;

	public BranchCodePoint(ConditionalJump condition)
	{
		this.condition = condition;
	}
	
	public CodePoint getTakenSuccessor()
	{
		return taken;
	}

	public void setTakenSuccessor(CodePoint takenSuccessor)
	{
		this.taken = takenSuccessor;
	}

	public CodePoint getNotTakenSuccessor()
	{
		return fallthrough;
	}

	public void setNotTakenSuccessor(CodePoint notTakenSuccessor)
	{
		this.fallthrough = notTakenSuccessor;
	}
	
	public ConditionalJump getCondition()
	{
		return condition;
	}
	
	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
	
	@Override
	public String toString()
	{
		return condition.toString() + " - " + liveSet.toString();
	}
}
