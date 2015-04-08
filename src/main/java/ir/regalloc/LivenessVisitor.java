package ir.regalloc;

import java.util.BitSet;

import ir.cfgraph.BottomUpVisitor;
import ir.cfgraph.BranchCodePoint;
import ir.cfgraph.CodePoint;
import ir.cfgraph.LinearCodePoint;
import ir.ops.FunctionDeclaration;
import ir.ops.Statement;

public class LivenessVisitor extends BottomUpVisitor
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
			liveness.or(liveness);
		}
		previousLivenessFlags = codePoint.getLiveness();
		
		for(String liveId : statementVisitor.getLiveSet())
			liveness.set(getIdentifierIndex(liveId));
		for(String deadId : statementVisitor.getDeadSet())
			liveness.clear(getIdentifierIndex(deadId));
	}
	
	@Override
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
