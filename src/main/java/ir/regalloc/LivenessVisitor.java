package ir.regalloc;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.CodePoint;
import ir.cfgraph.LinearCodePoint;
import ir.ops.Statement;

import java.util.HashSet;

public class LivenessVisitor extends BottomUpVisitor
{	
	private HashSet<String> ignoreIds = new HashSet<String>();
	
	public void addIgnoredId(String id)
	{
		ignoreIds.add(id);
	}
	
	private StatementVisitor statementVisitor = new StatementVisitor();
	private HashSet<String> previousLivenessFlags;
	private void setLivenessFlag(Statement statement, CodePoint codePoint)
	{
		statementVisitor.clear();
		statement.accept(statementVisitor);
		HashSet<String> liveSet = codePoint.getLiveSet();
		
		if(previousLivenessFlags != null)
		{
			liveSet.addAll(previousLivenessFlags);
		}
		
		for(String liveId : statementVisitor.getLiveSet())
			if(!ignoreIds.contains(liveId))
				liveSet.add(liveId);
		for(String deadId : statementVisitor.getDeadSet())
			liveSet.remove(deadId);
	}	
	
	@Override
	protected void beforeParent(CodePoint codePoint)
	{
		previousLivenessFlags = codePoint.getLiveSet();
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
