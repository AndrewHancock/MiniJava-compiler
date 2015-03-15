package ir.cfgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ir.TempAllocator;
import ir.ops.Expression;
import ir.ops.Identifier;
import ir.ops.RelationalOp;
import ir.ops.Statement;

public class FunctionBuilder implements TemporaryProvider
{
	private TempAllocator tempAllocator = new TempAllocator();
	private List<Statement> statements = new ArrayList<Statement>();
	
	public FunctionBuilder()
	{
		
	}
	
	public List<Identifier> getTemporaries()
	{
		return tempAllocator.getTemporaries();
	}
	
	public void addStatement(Statement statement)
	{
		statements.add(statement);
	}

	@Override
	public Identifier getTemporary()
	{
		return tempAllocator.GetTemporary();
	}
	
	public List<Statement> getStatements()
	{
		return statements;
	}
}
