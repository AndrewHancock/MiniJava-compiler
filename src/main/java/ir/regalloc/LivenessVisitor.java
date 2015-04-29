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
	private Map<CodePoint, Set<String>> livenessMap = new HashMap<CodePoint, Set<String>>();
	private Set<String> previousLivenessFlags = new HashSet<String>(); 
	@Override
	public void clear()
	{
		super.clear();
		livenessMap.clear();		
	}

	private StatementVisitor statementVisitor = new StatementVisitor();
	private void setLivenessFlag(Statement statement, CodePoint codePoint)
	{
		statementVisitor.clear();
		statement.accept(statementVisitor);

		Set<String> liveSet = livenessMap.get(codePoint);
		if (liveSet == null)
		{
			liveSet = new HashSet<String>();
			livenessMap.put(codePoint, liveSet);
		}

		if (!previousLivenessFlags.isEmpty())
		{
			liveSet.addAll(previousLivenessFlags);
			previousLivenessFlags.clear();
		}		

		for (String liveId : statementVisitor.getLiveSet())
			liveSet.add(liveId);
		previousLivenessFlags.addAll(liveSet);
		for (String deadId : statementVisitor.getDeadSet())		
			previousLivenessFlags.remove(deadId); // Dead propegate up
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
