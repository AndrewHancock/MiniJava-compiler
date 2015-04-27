package ir.regalloc;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.CodePoint;
import ir.cfgraph.LinearCodePoint;
import ir.ops.Statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LivenessVisitor extends BottomUpVisitor
{	
	private HashSet<String> ignoreIds = new HashSet<String>();
	private Map<CodePoint, Set<String>> livenessMap = new HashMap<CodePoint, Set<String>>();
	
	public void addIgnoredId(String id)
	{
		ignoreIds.add(id);
		livenessMap = new HashMap<CodePoint, Set<String>>();
		previousLivenessFlags = null;
	}
	
	private StatementVisitor statementVisitor = new StatementVisitor();
	private Set<String> previousLivenessFlags;
	private void setLivenessFlag(Statement statement, CodePoint codePoint)
	{
		statementVisitor.clear();		
		statement.accept(statementVisitor);
		
		Set<String> liveSet = livenessMap.get(codePoint);
		if(liveSet == null)
		{
			liveSet = new HashSet<String>();
			livenessMap.put(codePoint, liveSet);
		}
		
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
	
	public Map<CodePoint, Set<String>> getLivenessMap()
	{
		return livenessMap;
	}
	@Override
	protected void beforeParent(CodePoint codePoint)
	{
		previousLivenessFlags = livenessMap.get(codePoint);
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
