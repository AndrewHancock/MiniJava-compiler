package visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import symboltable.RamClass;
import symboltable.Table;
import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.Assign;
import syntaxtree.Call;
import syntaxtree.ClassDeclSimple;
import syntaxtree.False;
import syntaxtree.Formal;
import syntaxtree.If;
import syntaxtree.IntegerLiteral;
import syntaxtree.LessThan;
import syntaxtree.LessThanOrEqual;
import syntaxtree.MainClass;
import syntaxtree.MethodDecl;
import syntaxtree.Minus;
import syntaxtree.NewArray;
import syntaxtree.NewObject;
import syntaxtree.Not;
import syntaxtree.Or;
import syntaxtree.Plus;
import syntaxtree.Print;
import syntaxtree.PrintLn;
import syntaxtree.This;
import syntaxtree.Times;
import syntaxtree.True;
import syntaxtree.VarDecl;
import ir.cfgraph.ControlFlowGraphBuilder;
import ir.cfgraph.Frame;
import ir.ops.ArrayAssignment;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.DataType;
import ir.ops.Identifier;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.SysCall;
import ir.ops.Expression;
import ir.ops.BinOp.Op;

public class IrGenerator extends DepthFirstVisitor
{
	private Table table;

	public IrGenerator(Table symTable)
	{
		this.table = symTable;
	}

	private final String currentNamespace = "minijava";
	private Frame currentFrame;
	private Collection<Frame> frameList = new ArrayList<Frame>();
	private Collection<RecordDeclaration> recordList = new ArrayList<RecordDeclaration>();
	private ControlFlowGraphBuilder cfgBuilder;

	public Collection<Frame> getFrameList()
	{
		return frameList;
	}

	public Collection<RecordDeclaration> getRecordList()
	{
		return recordList;
	}

	public void visit(MainClass m)
	{
		currentFrame = new Frame("", "main", 0, 0);
		cfgBuilder = new ControlFlowGraphBuilder(currentFrame);
		m.s.accept(this);
		currentFrame.setStartingBlock(cfgBuilder.getStartingBlock());
		frameList.add(currentFrame);
	}

	@Override
	public void visit(MethodDecl d)
	{

		currentFrame = new Frame(currentNamespace, d.i.s, d.fl.size(), d.vl.size());
		cfgBuilder = new ControlFlowGraphBuilder(currentFrame);

		currentLocals.clear();

		// For any method declaration, "this" is implicit
		currentFrame.getParams().add(new Identifier("this"));
		currentLocals.put("this", new Identifier("this"));

		for (int i = 0; i < d.vl.size(); i++)
		{
			currentFrame.getLocals().add(new Identifier(d.vl.elementAt(i).i.s));
			d.vl.elementAt(i).accept(this);
		}

		for (int i = 0; i < d.fl.size(); i++)
		{
			currentFrame.getParams().add(new Identifier(d.fl.elementAt(i).i.s));
			d.fl.elementAt(i).accept(this);
		}

		for (int i = 0; i < d.sl.size(); i++)
			d.sl.elementAt(i).accept(this);

		d.e.accept(this);
		cfgBuilder.addStatement(new Return(currentOperand));

		currentFrame.setStartingBlock(cfgBuilder.getStartingBlock());
		frameList.add(currentFrame);
	}

	@Override
	public void visit(Print p)
	{
		List<Expression> parameters = new ArrayList<Expression>();
		for (int i = 0; i < p.e.size(); i++)
		{
			p.e.elementAt(i).accept(this);
			parameters.add(currentOperand);
		}
		currentOperand = currentFrame.getTemporary();
		cfgBuilder.addStatement(new Assignment(new SysCall("print", parameters),
				currentOperand));
	}

	public void visit(PrintLn p)
	{
		List<Expression> parameters = new ArrayList<Expression>();
		for (int i = 0; i < p.list.size(); i++)
		{
			p.list.elementAt(i).accept(this);
			parameters.add(currentOperand);
		}

		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new SysCall("println", parameters),
				currentOperand));
	}

	Expression currentOperand = null;

	@Override
	public void visit(IntegerLiteral l)
	{
		currentOperand = new ir.ops.IntegerLiteral(l.i);
	}

	public void visit(Plus p)
	{
		p.e1.accept(this);
		Expression leftOperand = currentOperand;
		p.e2.accept(this);
		Expression rightOperand = currentOperand;

		Identifier dest = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new BinOp(Op.ADD, leftOperand,
				rightOperand), dest));
		currentOperand = dest;
	}

	public void visit(Times t)
	{
		t.e1.accept(this);
		Expression leftOperand = currentOperand;
		t.e2.accept(this);
		Expression rightOperand = currentOperand;

		Identifier dest = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new BinOp(Op.MULT, leftOperand,
				rightOperand), dest));
		currentOperand = dest;
	}

	public void visit(Minus m)
	{
		m.e1.accept(this);
		Expression leftOperand = currentOperand;
		m.e2.accept(this);
		Expression rightOperand = currentOperand;

		Identifier dest = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new BinOp(Op.SUBTRACT, leftOperand,
				rightOperand), dest));
		currentOperand = dest;
	}

	private RamClass currentClass;	
	HashMap<String, RecordDeclaration> recordMap = new HashMap<String, RecordDeclaration>();

	public void visit(ClassDeclSimple c)
	{
		currentClass = table.getClass(c.i.s);
		
		RecordDeclaration declaration = new RecordDeclaration(currentNamespace,
				currentClass.getId());
		for (int i = 0; i < c.vl.size(); i++)
		{
			currentClass.getVar(c.vl.elementAt(i).i.s).setMemoryOffset(i);
			declaration.addField(DataType.INT);
		}
		recordList.add(declaration);
		recordMap.put(c.i.s, declaration);

		for (int i = 0; i < c.ml.size(); i++)
		{
			c.ml.elementAt(i).accept(this);
		}

	}

	@Override
	public void visit(NewArray a)
	{
		a.e.accept(this);
		ir.ops.NewArray newArray = new ir.ops.NewArray(DataType.INT, currentOperand);
		currentOperand = newArray;
	}

	@Override
	public void visit(Assign a)
	{
		a.e.accept(this);
		Expression src = currentOperand;
		a.i.accept(this);
		Expression dest = currentOperand;
		cfgBuilder.addStatement(new Assignment(src, dest));
	}

	@Override
	public void visit(ArrayAssign a)
	{
		a.e2.accept(this);
		Expression sourceOperand = currentOperand;
		a.e1.accept(this);
		Expression index = currentOperand;
		cfgBuilder.addStatement(new ArrayAssignment(sourceOperand, new Identifier(
				a.i.s), index));
	}

	@Override
	public void visit(Call c)
	{
		ArrayList<Expression> params = new ArrayList<Expression>(c.el.size() + 1);

		// Always pass this pointer first
		c.e.accept(this);
		params.add(currentOperand);

		for (int i = 0; i < c.el.size(); i++)
		{
			c.el.elementAt(i).accept(this);
			params.add(currentOperand);
		}

		currentOperand = cfgBuilder.getTemporary();
		ir.ops.Call call = new ir.ops.Call(c.i.s, params);
		cfgBuilder.addStatement(new Assignment(call, currentOperand));
	}

	@Override
	public void visit(And a)
	{
		a.e1.accept(this);

		Identifier result = cfgBuilder.getTemporary();
		// Short circuit - when expression 1 is "false", we do nothing further.
		cfgBuilder.addCondition(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.beginTrueBlock();
		a.e2.accept(this);

		cfgBuilder.addCondition(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		currentOperand = result;

		cfgBuilder.endConditional();
		cfgBuilder.endConditional();
	}

	@Override
	public void visit(Or a)
	{
		a.e1.accept(this);

		Identifier result = cfgBuilder.getTemporary();
		// Short circuit - when expression 0 is "false", we do nothing further.
		cfgBuilder.addCondition(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(0)));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		cfgBuilder.beginFalseBlock();
		a.e2.accept(this);

		cfgBuilder.addCondition(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		currentOperand = result;

		cfgBuilder.endConditional();
		cfgBuilder.endConditional();
	}

	@Override
	public void visit(NewObject n)
	{
		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new RecordAllocation(
				currentNamespace, n.i.s), currentOperand));
	}

	HashMap<String, Expression> currentLocals = new HashMap<String, Expression>();

	public void visit(VarDecl v)
	{
		currentLocals.put(v.i.s, new Identifier(v.i.s));
	}

	public void visit(Formal f)
	{
		currentLocals.put(f.i.s, new Identifier(f.i.s));
	}

	
	@Override
	public void visit(syntaxtree.IdentifierExp e)
	{
		Expression result = null;
		RecordDeclaration record = null;
		if((result = currentLocals.get(e.s)) != null)
		{
			currentOperand = result;
			return;
		}
		else if((record = recordMap.get(currentClass.getId())) != null)
		{
			currentClass.getVar(e.s).getMemoryOffset();
			currentOperand = new RecordAccess("minijava", currentClass.getId(), new Identifier("this"), currentClass.getVar(e.s).getMemoryOffset());
		}
	}
	
	@Override
	public void visit(syntaxtree.Identifier e)
	{
		
		Expression result = null;
		RecordDeclaration record = null;
		if((result = currentLocals.get(e.s)) != null)
		{
			currentOperand = result;
			return;
		}
		else if((record = recordMap.get(currentClass.getId())) != null)
		{
			currentClass.getVar(e.s).getMemoryOffset();
			currentOperand = new RecordAccess("minijava", currentClass.getId(), new Identifier("this"), currentClass.getVar(e.s).getMemoryOffset());
		}
	}

	public void visit(This t)
	{
		currentOperand = currentLocals.get("this");
	}

	public void visit(If i)
	{
		i.e.accept(this);
		cfgBuilder.addCondition(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));

		cfgBuilder.beginTrueBlock();
		i.s1.accept(this);

		cfgBuilder.beginFalseBlock();
		i.s2.accept(this);

		cfgBuilder.endConditional();
	}

	public void visit(LessThanOrEqual l)
	{
		l.e1.accept(this);
		Expression leftOperand = currentOperand;
		l.e2.accept(this);
		Expression rightOperand = currentOperand;

		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new RelationalOp(RelationalOp.Op.LTE,
				leftOperand, rightOperand), currentOperand));
	}

	public void visit(LessThan l)
	{
		l.e1.accept(this);
		Expression leftOperand = currentOperand;
		l.e2.accept(this);
		Expression rightOperand = currentOperand;

		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new RelationalOp(RelationalOp.Op.LT,
				leftOperand, rightOperand), currentOperand));
	}

	public void visit(False f)
	{
		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new ir.ops.IntegerLiteral(0),
				currentOperand));
	}

	public void visit(True t)
	{
		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new ir.ops.IntegerLiteral(1),
				currentOperand));
	}

	public void visit(Not n)
	{
		n.e.accept(this);
		Identifier result = cfgBuilder.getTemporary();
		cfgBuilder.addCondition(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.endConditional();
		currentOperand = result;
	}
}
