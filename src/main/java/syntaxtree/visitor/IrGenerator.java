package syntaxtree.visitor;

import ir.ops.ArrayAccess;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.BinOp.Op;
import ir.ops.ConditionalJump;
import ir.ops.DataType;
import ir.ops.Expression;
import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;
import ir.ops.Jump;
import ir.ops.Label;
import ir.ops.RecordAccess;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.Statement;
import ir.ops.SysCall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import symboltable.RamClass;
import symboltable.RamMethod;
import symboltable.RamVariable;
import symboltable.Table;
import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.Assign;
import syntaxtree.Call;
import syntaxtree.ClassDeclSimple;
import syntaxtree.Equality;
import syntaxtree.False;
import syntaxtree.ForEach;
import syntaxtree.Formal;
import syntaxtree.IdentifierType;
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
import syntaxtree.While;

public class IrGenerator extends DepthFirstVisitor
{
	private Table table;

	public IrGenerator(Table symTable)
	{
		this.table = symTable;
	}

	private final String currentNamespace = "minijava";
	private FunctionDeclaration currentFunction;
	private Collection<FunctionDeclaration> functionDeclarations = new ArrayList<FunctionDeclaration>();
	private Collection<RecordDeclaration> recordDeclarations = new ArrayList<RecordDeclaration>();

	public Collection<FunctionDeclaration> getFrameList()
	{
		return functionDeclarations;
	}

	public Collection<RecordDeclaration> getRecordList()
	{
		return recordDeclarations;
	}

	private void addStatement(Statement s)
	{
		currentFunction.getStatements().add(s);
	}

	private int tempCounter;

	private Identifier getNewTemporary()
	{
		String tempName;
		do
		{
			tempName = "t" + tempCounter++;
		} 
		while (currentLocals.containsKey(tempName));
		Identifier temp = new Identifier(tempName);
		currentFunction.getTemporaries().add(new Identifier(tempName));
		return temp;
	}

	public void visit(MainClass m)
	{
		tempCounter = 0;
		currentFunction = new FunctionDeclaration(m.i1.s, "main");
		m.s.accept(this);
		functionDeclarations.add(currentFunction);
	}

	private RamMethod currentMethod;

	@Override
	public void visit(MethodDecl d)
	{
		tempCounter = 0;
		currentMethod = currentClass.getMethod(d.i.s);

		currentLocals.clear();
		currentLocals.put("this", new Identifier("this"));

		currentFunction = new FunctionDeclaration(currentClass.getId(), d.i.s);

		currentFunction.getParams().add(new Identifier("this"));

		for (VarDecl var : d.vl)
		{
			currentFunction.getLocals().add(new Identifier(var.i.s));
			var.accept(this);
		}

		for (Formal formal : d.fl)
		{
			currentFunction.getParams().add(new Identifier(formal.i.s));
			formal.accept(this);
		}

		for (syntaxtree.Statement statement : d.sl)
		{
			statement.accept(this);
		}
		d.e.accept(this);
		addStatement(new Return(currentOperand));

		functionDeclarations.add(currentFunction);
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
		currentOperand = getNewTemporary();
		addStatement(new Assignment(new SysCall("system", "print", parameters),
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

		currentOperand = getNewTemporary();
		addStatement(new Assignment(new SysCall("system", "println", parameters),
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

		Identifier dest = getNewTemporary();
		addStatement(new Assignment(new BinOp(Op.ADD, leftOperand, rightOperand),
				dest));
		currentOperand = dest;
	}

	public void visit(Times t)
	{
		t.e1.accept(this);
		Expression leftOperand = currentOperand;
		t.e2.accept(this);
		Expression rightOperand = currentOperand;

		Identifier dest = getNewTemporary();
		addStatement(new Assignment(new BinOp(Op.MULT, leftOperand, rightOperand),
				dest));
		currentOperand = dest;
	}

	public void visit(Minus m)
	{
		m.e1.accept(this);
		Expression leftOperand = currentOperand;
		m.e2.accept(this);
		Expression rightOperand = currentOperand;

		Identifier dest = getNewTemporary();
		addStatement(new Assignment(
				new BinOp(Op.SUBTRACT, leftOperand, rightOperand), dest));
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
			currentClass.getVar(c.vl.get(i).i.s).setMemoryOffset(i);
			declaration.addField(DataType.INT);
		}
		recordDeclarations.add(declaration);
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
		Identifier dest = getNewTemporary(); 
		addStatement(new Assignment(new ir.ops.ArrayAllocation(DataType.INT, currentOperand), dest));
		currentOperand = dest;
	}

	@Override
	public void visit(ArrayLookup n)
	{
		n.e1.accept(this);
		Expression array = currentOperand;
		n.e2.accept(this);
		Expression index = currentOperand;
		
		Identifier idx = getNewTemporary();
		addStatement(new Assignment(index, idx));
		Identifier dest = getNewTemporary();
		addStatement(new Assignment(new ArrayAccess(array, DataType.INT, idx), dest));
		currentOperand = dest;
	}

	@Override
	public void visit(Assign a)
	{
		a.e.accept(this);
		Expression src = currentOperand;
		a.i.accept(this);
		Expression dest = currentOperand;
		addStatement(new Assignment(src, dest));
	}

	@Override
	public void visit(ArrayAssign a)
	{
		a.e2.accept(this);
		Expression src = currentOperand;
		a.e1.accept(this);
		Expression index = currentOperand;
		a.i.accept(this);
		Expression target = currentOperand;

		Identifier idx = getNewTemporary();
		addStatement(new Assignment(index, idx));
		
		Identifier temp = getNewTemporary();
		addStatement(new Assignment(target, temp));
		
		addStatement(new Assignment(src, new ArrayAccess(temp, DataType.INT, idx)));
	}

	@Override
	public void visit(Call c)
	{
		ArrayList<Expression> params = new ArrayList<Expression>(c.el.size() + 1);

		// Always pass this pointer first
		c.e.accept(this);
		String methodClassName = currentClassName;
		params.add(currentOperand);

		for (int i = 0; i < c.el.size(); i++)
		{
			c.el.elementAt(i).accept(this);
			params.add(currentOperand);
		}

		currentOperand = getNewTemporary();
		ir.ops.Call call = new ir.ops.Call(methodClassName, c.i.s, params);
		addStatement(new Assignment(call, currentOperand));
	}

	int andCount;
	@Override
	public void visit(And a)
	{
		int count = andCount++;
		Label falseLabel = new Label("and_false_" + count);
		Label endLabel = new Label("and_end_" + count);
		a.e1.accept(this);

		Identifier result = getNewTemporary();

		// Short circuit - when expression 1 is "false", we do nothing further.
		addStatement(new ConditionalJump(new RelationalOp(RelationalOp.Op.EQ,
				currentOperand, new ir.ops.IntegerLiteral(0)), falseLabel));
		a.e2.accept(this);
		addStatement(new ConditionalJump(new RelationalOp(RelationalOp.Op.EQ,
				currentOperand, new ir.ops.IntegerLiteral(0)), falseLabel));
		addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		addStatement(new Jump(endLabel));
		addStatement(falseLabel);
		addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		addStatement(endLabel);
	}

	int orCount;
	@Override
	public void visit(Or a)
	{
		int count = orCount++;
		Label trueLabel = new Label("or_true_" + count);
		Label endLabel = new Label("or_end_" + count);
		
		a.e1.accept(this);
		Identifier result = getNewTemporary();
		// Short circuit - when expression 1 is "false", we do nothing further.
		addStatement(new ConditionalJump(new RelationalOp(RelationalOp.Op.EQ,
				currentOperand, new ir.ops.IntegerLiteral(1)), trueLabel));
		a.e2.accept(this);
		addStatement(new ConditionalJump(new RelationalOp(RelationalOp.Op.EQ,
				currentOperand, new ir.ops.IntegerLiteral(1)), trueLabel));
		addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));		
		addStatement(new Jump(endLabel));
		addStatement(trueLabel);
		addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));		

		addStatement(endLabel);
	}

	String currentClassName;

	@Override
	public void visit(NewObject n)
	{
		currentClassName = n.i.s;
		currentOperand = getNewTemporary();
		addStatement(new Assignment(new RecordAllocation(currentNamespace, n.i.s),
				currentOperand));
	}

	private HashMap<String, Identifier> currentLocals = new HashMap<String, Identifier>();

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
		RamVariable result = null;
		if ((result = currentMethod.getVar(e.s)) != null
				|| (result = currentMethod.getParam(e.s)) != null)
		{
			if (result.type() instanceof IdentifierType)
				currentClassName = ((IdentifierType) result.type()).s;

			currentOperand = currentLocals.get(e.s);
			return;
		}
		else if ((result = currentClass.getVar(e.s)) != null)
		{
			if (result.type() instanceof IdentifierType)
				currentClassName = ((IdentifierType) result.type()).s;

			Identifier dest = getNewTemporary();
			addStatement(new Assignment(new RecordAccess("minijava", currentClass.getId(),
					new Identifier("this"), result.getMemoryOffset()), dest));
			currentOperand = dest;
		}

	}

	@Override
	public void visit(syntaxtree.Identifier e)
	{

		Expression result = null;
		if ((result = currentLocals.get(e.s)) != null)
		{
			currentOperand = result;
			return;
		}
		else if (recordMap.get(currentClass.getId()) != null)
		{
			currentClass.getVar(e.s).getMemoryOffset();
			currentOperand = new RecordAccess("minijava", currentClass.getId(),
					new Identifier("this"), currentClass.getVar(e.s)
							.getMemoryOffset());			
		}
	}

	public void visit(This t)
	{
		currentOperand = currentLocals.get("this");
	}

	int ifCount;
	@Override
	public void visit(If i)
	{
		int count = ifCount++;
		Label trueLabel = new Label("if_true_" + count);
		Label endLabel = new Label("if_end_" + count);
		
		i.e.accept(this);
		addStatement(new ConditionalJump(new RelationalOp(RelationalOp.Op.EQ,
				currentOperand, new ir.ops.IntegerLiteral(1)), trueLabel));
		i.s2.accept(this);
		addStatement(new Jump(endLabel));
		addStatement(trueLabel);
		i.s1.accept(this);
		addStatement(endLabel);
	}

	@Override
	public void visit(LessThanOrEqual l)
	{
		l.e1.accept(this);
		Expression leftOperand = currentOperand;
		l.e2.accept(this);
		Expression rightOperand = currentOperand;

		currentOperand = getNewTemporary();
		addStatement(new Assignment(new RelationalOp(RelationalOp.Op.LTE,
				leftOperand, rightOperand), currentOperand));
	}

	@Override
	public void visit(LessThan l)
	{
		l.e1.accept(this);
		Expression leftOperand = currentOperand;
		l.e2.accept(this);
		Expression rightOperand = currentOperand;

		currentOperand = getNewTemporary();
		addStatement(new Assignment(new RelationalOp(RelationalOp.Op.LT,
				leftOperand, rightOperand), currentOperand));
	}

	@Override
	public void visit(Equality e)
	{
		e.e1.accept(this);
		Expression leftOperand = currentOperand;
		e.e2.accept(this);
		Expression rightOperand = currentOperand;

		currentOperand = getNewTemporary();
		addStatement(new Assignment(new RelationalOp(RelationalOp.Op.EQ,
				leftOperand, rightOperand), currentOperand));
	}

	@Override
	public void visit(False f)
	{
		currentOperand = new ir.ops.IntegerLiteral(0);
	}

	@Override
	public void visit(True t)
	{
		currentOperand = new ir.ops.IntegerLiteral(1);
	}

	private int notCount;
	@Override
	public void visit(Not n)
	{
		int count = notCount++;
		Label trueLabel = new Label("not_true_" + count);		
		Label endLabel = new Label("not_end_" + count);
		n.e.accept(this);
		Identifier result = getNewTemporary();
		addStatement(new ConditionalJump(new RelationalOp(RelationalOp.Op.EQ,
				currentOperand, new ir.ops.IntegerLiteral(1)), trueLabel));
		addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		addStatement(new Jump(endLabel));
		addStatement(trueLabel);
		addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		addStatement(endLabel);
		currentOperand = result;
	}

	private int whileCount;
	@Override
	public void visit(While n)
	{
		int count = whileCount++;
		Label testLabel = new Label("while_test_" + count);
		Label endLabel = new Label("while_end_" + count);
		addStatement(testLabel);
		n.e.accept(this);
		addStatement(new ConditionalJump(new RelationalOp(RelationalOp.Op.EQ,
				currentOperand, new ir.ops.IntegerLiteral(0)), endLabel));
		n.s.accept(this);
		addStatement(new Jump(testLabel));
		addStatement(endLabel);
	}


	@Override
	public void visit(ForEach n)
	{
		// Planning to remove for loop
	}

	@Override
	public void visit(ArrayLength l)
	{
		super.visit(l);
		Identifier dest = getNewTemporary();		
		addStatement(new Assignment(new ir.ops.ArrayLength(currentOperand), dest));
		currentOperand = dest;
	}
}
