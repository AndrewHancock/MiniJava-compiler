package ir.regalloc;

import ir.ops.ArrayAccess;
import ir.ops.ArrayAllocation;
import ir.ops.ArrayLength;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.Call;
import ir.ops.ConditionalJump;
import ir.ops.Expression;
import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;
import ir.ops.IntegerLiteral;
import ir.ops.Jump;
import ir.ops.Label;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.SysCall;
import ir.visitor.IrVisitor;

import java.util.HashSet;

public class StatementVisitor implements IrVisitor
{
	private HashSet<String> liveSet = new HashSet<String>();
	private HashSet<String> deadSet = new HashSet<String>();	
			
	public void clear()
	{
		liveSet.clear();
		deadSet.clear();		
	}
	
	@Override
	public void visit(ArrayAccess a)
	{
		lhs = false;
		a.getIndex().accept(this);
		a.getReference().accept(this);		
	}

	@Override
	public void visit(ArrayAllocation n)
	{
		n.getSize().accept(this);

	}

	@Override
	public void visit(ArrayLength a)
	{
		a.getExpression().accept(this);
	}

	private boolean lhs;
	@Override
	public void visit(Assignment assignment)
	{
		assignment.getSrc().accept(this);
		lhs = true;
		assignment.getDest().accept(this);
		lhs = false;
	}

	@Override
	public void visit(BinOp b)
	{
		b.getSrc1().accept(this);
		b.getSrc2().accept(this);
	}

	@Override
	public void visit(Call c)
	{
		for(Expression param : c.getParameters())
			param.accept(this);		
	}

	@Override
	public void visit(ConditionalJump j)
	{
		j.getCondition().accept(this);
	}

	@Override
	public void visit(FunctionDeclaration f)
	{
		throw new RuntimeException("Not Implemented.");
	}

	@Override
	public void visit(Identifier i)
	{
		if(lhs)
			deadSet.add(i.getId());
		else
			liveSet.add(i.getId());
	}

	@Override
	public void visit(IntegerLiteral l)
	{
		// Nothing to do for integer literal.
	}

	@Override
	public void visit(Jump j)
	{	

	}

	@Override
	public void visit(Label l)
	{
	
	}

	@Override
	public void visit(RecordAccess r)
	{
		lhs = false;
		r.getIdentifier().accept(this);		
	}

	@Override
	public void visit(RecordAllocation a)
	{

	}

	@Override
	public void visit(RecordDeclaration r)
	{	

	}

	@Override
	public void visit(RelationalOp r)
	{
		r.getSrc1().accept(this);
		r.getSrc2().accept(this);
	}

	@Override
	public void visit(Return r)
	{
		r.getSource().accept(this);
	}

	@Override
	public void visit(SysCall s)
	{
		for(Expression param : s.getParameters())
			param.accept(this);
	}

	public HashSet<String> getLiveSet()
	{
		return liveSet;
	}

	public HashSet<String> getDeadSet()
	{
		return deadSet;
	}

	
}
