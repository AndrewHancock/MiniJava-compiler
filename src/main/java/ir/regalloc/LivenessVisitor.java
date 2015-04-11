package ir.regalloc;

import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.CodePoint;
import ir.cfgraph.LinearCodePoint;
import ir.cfgraph.Visitor;
import ir.ops.FunctionDeclaration;
import ir.ops.Statement;

import java.util.BitSet;
import java.util.HashSet;

public class LivenessVisitor implements Visitor
{		
	private FunctionDeclaration func;
	
	public void setFunctionDeclaration(FunctionDeclaration func)
	{
		this.func = func;
	}

	private int getIdentifierIndex(String id)
	{
		for(int i = 0; i < func.getLocals().size(); i++)
			if(func.getLocals().get(i).equals(id))
				return i;
		
		for(int i = func.getLocals().size(); i < func.getTemporaries().size(); i++)
			if(func.getTemporaries().get(i).equals(id))
				return i;
	
		throw new RuntimeException("Invalid identifier.");
	}
	
	private StatementVisitor statementVisitor = new StatementVisitor();
	private BitSet previousLivenessFlags;
	private void setLivenessFlag(Statement statement, CodePoint codePoint)
	{
		statementVisitor.clear();
		statement.accept(statementVisitor);
		BitSet liveness = codePoint.getLiveness();
		
		if(previousLivenessFlags != null)
		{
			liveness.or(previousLivenessFlags);
		}		
		
		for(String liveId : statementVisitor.getLiveSet())
			liveness.set(getIdentifierIndex(liveId));
		for(String deadId : statementVisitor.getDeadSet())
			liveness.clear(getIdentifierIndex(deadId));
	}
	
	
	protected void joinCallback(CodePoint codePoint)
	{
		previousLivenessFlags = codePoint.getLiveness();
	}
	
	
	@Override
	public void visit(LinearCodePoint codePoint)
	{	
		setLivenessFlag(codePoint.getStatement(), codePoint);
		for(CodePoint parent : codePoint.getParents())
		{
			previousLivenessFlags = codePoint.getLiveness();
			parent.accept(this);			
		}
	}
	
	private HashSet<BranchCodePoint> incompleteBranchSet = new HashSet<BranchCodePoint>();
	@Override
	public void visit(BranchCodePoint codePoint)
	{	
		setLivenessFlag(codePoint.getCondition(), codePoint);
		// A branch is always visited twice - once from the "taken" edge,
		// and once from the "not taken edge." The parents are only visited
		// after both branches have joined.
		if(! incompleteBranchSet.remove(codePoint))
		{
			incompleteBranchSet.add(codePoint);			
		}
		else
		{
			for(CodePoint parent : codePoint.getParents())
			{
				previousLivenessFlags = codePoint.getLiveness();
				parent.accept(this);
			}
		}		
	}
}
