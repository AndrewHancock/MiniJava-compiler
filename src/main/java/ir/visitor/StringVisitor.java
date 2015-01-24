package ir.visitor;

import java.io.PrintStream;

import ir.Temporary;
import ir.cfgraph.BasicBlock;
import ir.cfgraph.CodePoint;
import ir.cfgraph.ConditionalBasicBlock;
import ir.ops.ArrayAccess;
import ir.ops.ArrayAssignment;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.Call;
import ir.ops.Frame;
import ir.ops.Identifier;
import ir.ops.IntegerLiteral;
import ir.ops.NewArray;
import ir.ops.RecordAccess;
import ir.ops.RecordAssignment;
import ir.ops.RecordDeclaration;
import ir.ops.SysCall;
import ir.ops.Value;

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
		
		out.println("Temporaries: " + f.getTempAllocator().getTemporaryCount());
		for(Temporary temp : f.getTempAllocator().getTemporaries())
		{
			out.println("\t" + temp.getId());
		}
		f.getStartingBlock().accept(this);
	}

	@Override
	public void visit(BasicBlock b)
	{
		out.println("Basic Block:");
		for(CodePoint c : b.getCodePoints())		
			c.accept(this);	
		
		if(b.getChild() != null)
			b.getChild().accept(this);
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
		b.getDest().accept(this);
		
		out.print(" := ");
		
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
		case EQ:
			op = " == ";
			break;
		case NOT:
			op = " != ";		
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
		for(Value param : c.getParameters())
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
		boolean first = true;
		out.print(s.getId() + "(");
		for(Value param : s.getParameters())
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
	public void visit(Identifier i)
	{
		out.print(i.getId());
		
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
		out.print("[" + a.getIndex() + "]" );
		
	}

	@Override
	public void visit(ArrayAssignment a)
	{		
		
	}

	@Override
	public void visit(NewArray n)
	{	
		
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
		out.print(r.getId() + "[" + r.getIndex() + "]" );
		
	}

	@Override
	public void visit(RecordAssignment r)
	{
		r.getDest().accept(this);
		out.print(" := ");
		r.getSrc();		
	}

	private int conditionCounter = 0;
	@Override
	public void visit(ConditionalBasicBlock b)
	{
		int c = conditionCounter++;
		String label = "condiition_" + c;
		out.print(label + ":") ;
		for(CodePoint cp : b.getCodePoints())
			cp.accept(this);
		out.print(label + "_false:");
		b.getFalseBlock().accept(this);
		out.print(label + "_true");
		b.getTrueBlock().accept(this);
		
		
	}
}
