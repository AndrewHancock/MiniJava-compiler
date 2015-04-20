package ir.regalloc;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.CodePoint;
import ir.cfgraph.LinearCodePoint;
import ir.ops.FunctionDeclaration;
import ir.ops.Statement;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;

public class LivenessVisitor extends BottomUpVisitor
{		
	
	int nodeCount;
	public void setNodeCount(int count)
	{
		nodeCount = count;
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
		super.visit(codePoint);
	}	
	
	@Override
	public void visit(BranchCodePoint codePoint)
	{	
		setLivenessFlag(codePoint.getCondition(), codePoint);
		super.visit(codePoint);		
	}
}
