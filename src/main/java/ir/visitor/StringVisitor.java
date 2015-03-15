package ir.visitor;

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
import ir.ops.Statement;
import ir.ops.SysCall;

import java.io.PrintStream;
import java.util.Stack;

public class StringVisitor implements IrVisitor
{
	private IrPrintStream out;

	public StringVisitor(PrintStream out)
	{
		this.out = new IrPrintStream(out);
	}

	@Override
	public void visit(FunctionDeclaration f)
	{
		out.println("\n.namespace " + f.getNamespace() + " " + f.getId() + ":");
		out.println("Locals: ");
		for (Identifier value : f.getLocals())
		{
			out.println("\t" + value.getId());
		}
		out.println("Params: " + f.getParams().size());
		for (Identifier param : f.getParams())
		{
			out.println("\t" + param.getId());
		}

		out.println("Temporaries: " + f.getTemporaries().size());
		for (Identifier temp : f.getTemporaries())
		{
			out.println("\t" + temp.getId());
		}		
		out.println("Begin:");
		out.indent();
		for(Statement statement : f.getStatements())
		{
			statement.accept(this);
			out.println("");
		}
		out.unindent();

	}

	@Override
	public void visit(BinOp b)
	{
		String op;
		switch (b.getOp())
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

		if (b.getSrc2() != null)
			b.getSrc2().accept(this);
	}

	@Override
	public void visit(Call c)
	{
		boolean first = true;
		out.print(c.getId() + "(");
		for (Expression param : c.getParameters())
		{
			if (first)
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
		for (Expression param : s.getParameters())
		{
			if (first)
				first = false;
			else
				out.print(", ");
			param.accept(this);
		}
		out.print(")");
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
	public void visit(ArrayAllocation n)
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
		out.print("(" + r.getNamespace() + "." + r.getTypeName() + ")."
				+ r.getIdentifier().getId() + "[" + r.getFieldIndex() + "]");

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
		switch (r.getOp())
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

		if (r.getSrc2() != null)
			r.getSrc2().accept(this);
	}

	@Override
	public void visit(ArrayLength a)
	{
		a.getExpression().accept(this);
		out.print(".length");
	}

	@Override
	public void visit(ConditionalJump j)
	{
		out.print("jump if ");
		j.getCondition().accept(this);
		out.print(" to " + getNewLabel(j.getLabel()));
		
	}

	private int trueLabelCount;
	private int falseLabelCount;
	private int testLabelCount;
	private int bodyLabelCount;
	private int endLabelCount;
	
	Stack<String> trueLabels = new Stack<String>();
	Stack<String> falseLabels = new Stack<String>();
	Stack<String> testLabels = new Stack<String>();
	Stack<String> bodyLabels = new Stack<String>();
	Stack<String> endLabels = new Stack<String>();

	private String getNewLabel(Label label)
	{
		
		switch (label)
		{
		case TRUE:
			trueLabels.push("true_" + trueLabelCount++);
			return trueLabels.peek();			
		case FALSE:
			falseLabels.push("false_" + falseLabelCount++);
			return falseLabels.peek();
		case TEST:			
			return testLabels.pop();			
		case BODY:
			bodyLabels.push("body_" + bodyLabelCount++);
			return bodyLabels.peek();
		case END:
			endLabels.push("end_" + endLabelCount++);
			return endLabels.peek();
		default:
			throw new RuntimeException("Unrecognized Label Type.");
		}
	}

	@Override
	public void visit(Label label)
	{
		out.unindent();
		switch(label)
		{
		case TRUE:
			out.print(trueLabels.pop() +  ":");
			break;
		case FALSE:
			out.print(falseLabels.pop() +  ":");
			break;
		case TEST:
			testLabels.push("test_" + testLabelCount++);
			out.print(testLabels.peek() + ":");
			break;
		case BODY:
			out.print(bodyLabels.pop() + ":");
			break;
		case END:
			out.print(endLabels.pop() + ":");
			break;			
		default:
			throw new RuntimeException("Unrecognized label.");
		}
		out.indent();
	}

	@Override
	public void visit(Jump j)
	{
		out.print("jump " + getNewLabel(j.getLabel()));		
	}

}
