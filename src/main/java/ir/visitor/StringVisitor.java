package ir.visitor;

import java.io.PrintStream;

import ir.Temporary;
import ir.cfgraph.BasicBlock;
import ir.cfgraph.CodePoint;
import ir.cfgraph.Conditional;
import ir.cfgraph.Frame;
import ir.cfgraph.Loop;
import ir.ops.ArrayAccess;
import ir.ops.ArrayLength;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.Call;
import ir.ops.Identifier;
import ir.ops.IntegerLiteral;
import ir.ops.NewArray;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.SysCall;
import ir.ops.Expression;

public class StringVisitor implements IrVisitor
{
	private PrintStream out;
	
	public StringVisitor(PrintStream out)
	{
		this.out = out;
	}

	@Override
	public void visit(Frame f)
	{
		out.println("\n.namespace " + f.getNamespace() + " " + f.getId() + ":");
		out.println("Locals: ");
		for( Identifier value : f.getLocals())
		{
			out.println("\t" + value.getId());
		}
		out.println("Params: " + f.getParams().size());
		for(Identifier param : f.getParams())
		{
			out.println("\t" + param.getId());	
		}
		
		out.println("Temporaries: " + f.getTemporaries().size());
		for(Identifier temp : f.getTemporaries())
		{
			out.println("\t" + temp.getId());
		}
		out.println("Begin:");
		f.getStartingBlock().accept(this);
		
	}

	@Override
	public void visit(BasicBlock b)
	{		
		out.println("Begin block " + b.getId());
		for(CodePoint c : b.getCodePoints())		
			c.accept(this);
		
		if(b.getSuccessor() != null)
		{
			out.println("Successor: " +  b.getSuccessor().getId());
			b.getSuccessor().accept(this);
		}
		
		
	}

	@Override
	public void visit(CodePoint c)
	{
		out.print("\t");
		c.getOperation().accept(this);		
		out.println();
	}

	@Override
	public void visit(BinOp b)
	{		
		String op;
		switch(b.getOp())
		{
		case ADD:
			op = " + ";
			break;
		case SUBTRACT:
			op = " - ";
			break;
		case MULT:
			op = " * ";
			break;
		case AND:
			op = " & ";
			break;
		case OR:
			op = " | ";
			break;
		default:
			throw new RuntimeException("Unrecognized operation.");		
		}
		
		b.getSrc1().accept(this);
		out.print(op);
		
		if(b.getSrc2() != null)
			b.getSrc2().accept(this);
	}

	@Override
	public void visit(Call c)
	{
		boolean first = true;
		out.print(c.getId() + "(");
		for(Expression param : c.getParameters())
		{
			if(first)
				first = false;
			else
				out.print(", ");
			param.accept(this);
		}
		out.print(")");
		
	}

	@Override
	public void visit(Assignment assignment)
	{
		assignment.getDest().accept(this);
		out.print(" := ");
		assignment.getSrc().accept(this);
		
	}

	@Override
	public void visit(SysCall s)
	{
		boolean first;
		out.print(s.getId() + "(");
		for(Expression param : s.getParameters())
		{
			if(first = true)
				first = false;
			else
				out.print(", ");
			param.accept(this);
		}
		out.print(")");		
	}

	@Override
	public void visit(Temporary t)
	{
		out.print(t.getId());
		
	}

	@Override
	public void visit(IntegerLiteral l)
	{
		out.print(l.getValue());
		
	}

	@Override
	public void visit(ArrayAccess a)
	{
		a.getReference().accept(this);
		out.print("[");
		a.getIndex().accept(this);
		out.print("]"); 
		
	}

	@Override
	public void visit(NewArray n)
	{	
		out.print("new Int[" + n.getSize() + "]");
		
	}

	@Override
	public void visit(RecordDeclaration r)
	{
		out.println("Record:");
		out.println(r.getId());
		out.println("Fields:");
		out.println(r.getFieldCount());		
	}

	@Override
	public void visit(RecordAccess r)
	{
		out.print("(" + r.getNamespace() + "." + r.getTypeName() +")." + r.getIdentifier().getId() + "[" + r.getFieldIndex() + "]" );
		
	}

	private int conditionCounter = 0;
	@Override
	public void visit(Conditional b)
	{
		out.println("Begin block " + b.getId());
		int c = conditionCounter++;
		String label = "condiition_" + c;
		out.print(label + ":\n") ;
		out.print("\tif ");
		b.getTest().accept(this);
		out.println(":");
		
		
		out.println(label + "_false:");
		b.getFalseBlock().accept(this);
		out.println(label + "_true:");
		b.getTrueBlock().accept(this);		
		out.println(label + "_end");
		
		if(b.getSuccessor() != null)
		{
			out.println("Successor: " + b.getSuccessor().getId());
			b.getSuccessor().accept(this);
		}
		
	}

	@Override
	public void visit(RecordAllocation a)
	{
		out.print("new " + a.getTypeId());
		
	}

	@Override
	public void visit(Identifier i)
	{
		out.print(i.getId());		
	}

	@Override
	public void visit(Return r)
	{
		out.print("return ");
		r.getSource().accept(this);
		out.println("");
	}

	@Override
	public void visit(RelationalOp r)
	{

		String op;
		switch(r.getOp())
		{
		case LTE:
			op = " <= "; 
			break;
		case EQ:
			op = " == ";
			break;
		case LT:
			op = " < ";
			break;
		default:
			throw new RuntimeException("Unrecognized operation.");		
		}
		
		r.getSrc1().accept(this);
		out.print(op);
		
		if(r.getSrc2() != null)
			r.getSrc2().accept(this);		
	}

	@Override
	public void visit(Loop l)
	{
		out.println("Begin block " + l.getId());
		out.println("Loop test:");
		l.getTest().accept(this);
		out.print("Test Result: ");
		l.getTestResult().accept(this);
		out.println();
				
		out.println("Loop body:");
		l.getBody().accept(this);		
		if(l.getSuccessor() != null)
		{
			out.println("Successor: " + l.getSuccessor().getId());
			l.getSuccessor().accept(this);
		}
	}

	@Override
	public void visit(ArrayLength a)
	{
		a.getExpression().accept(this);
		out.println(".length");		
	}
	
	
}
