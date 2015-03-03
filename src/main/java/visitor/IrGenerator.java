package visitor;

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
import ir.cfgraph.FunctionBuilder;
import ir.cfgraph.Function;
import ir.ops.ArrayAccess;
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
	private Function currentFrame;
	private Collection<Function> frameList = new ArrayList<Function>();
	private Collection<RecordDeclaration> recordList = new ArrayList<RecordDeclaration>();
	private FunctionBuilder cfgBuilder;

	public Collection<Function> getFrameList()
	{
		return frameList;
	}

	public Collection<RecordDeclaration> getRecordList()
	{
		return recordList;
	}

	public void visit(MainClass m)
	{
		
		cfgBuilder = new FunctionBuilder();
		m.s.accept(this);
		currentFrame = new Function("", "main");
		for(Identifier id : cfgBuilder.getTemporaries())
			currentFrame.getTemporaries().add(id);
		currentFrame.setStartingBlock(cfgBuilder.getStartingBlock());
		frameList.add(currentFrame);
	}

	private RamMethod currentMethod;
	@Override
	public void visit(MethodDecl d)
	{
		currentMethod = currentClass.getMethod(d.i.s);
		cfgBuilder = new FunctionBuilder();

		currentLocals.clear();		
		currentLocals.put("this", new Identifier("this"));

		currentFrame = new Function(currentClass.getId(), d.i.s);
		
		currentFrame.getParams().add(new Identifier("this"));
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
		
		for(int i = 0; i < d.sl.size(); i++)
		{
			d.sl.elementAt(i).accept(this);
		}
		d.e.accept(this);
		cfgBuilder.addStatement(new Return(currentOperand));
		
		for(Identifier id : cfgBuilder.getTemporaries())
		{
			currentFrame.getTemporaries().add(id);			
		}

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
		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new SysCall("system", "print", parameters),
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
		cfgBuilder.addStatement(new Assignment(new SysCall("system", "println", parameters),
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
		currentOperand = new ir.ops.ArrayAllocation(DataType.INT, currentOperand);		
	}
	
	

	@Override
	public void visit(ArrayLookup n)
	{
		n.e1.accept(this);
		Expression array = currentOperand;
		n.e2.accept(this);
		Expression index = currentOperand;
		
		currentOperand = new ArrayAccess(array, DataType.INT, index);
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
		Expression src = currentOperand;
		a.e1.accept(this);
		Expression index = currentOperand;
		a.i.accept(this);
		Expression dest = currentOperand;
		
		cfgBuilder.addStatement(new Assignment(src, new ArrayAccess(dest, DataType.INT, index)));
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

		currentOperand = cfgBuilder.getTemporary();
		ir.ops.Call call = new ir.ops.Call(methodClassName, c.i.s, params);
		cfgBuilder.addStatement(new Assignment(call, currentOperand));
	}

	@Override
	public void visit(And a)
	{
		a.e1.accept(this);

		Identifier result = cfgBuilder.getTemporary();
		// Short circuit - when expression 1 is "false", we do nothing further.
		cfgBuilder.addBranch(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.beginTrueBlock();
		a.e2.accept(this);

		cfgBuilder.addBranch(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		currentOperand = result;

		cfgBuilder.endBranch();
		cfgBuilder.endBranch();
	}

	@Override
	public void visit(Or a)
	{
		a.e1.accept(this);

		Identifier result = cfgBuilder.getTemporary();
		// Short circuit - when expression 0 is "false", we do nothing further.
		cfgBuilder.addBranch(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(0)));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		cfgBuilder.beginFalseBlock();
		a.e2.accept(this);

		cfgBuilder.addBranch(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		currentOperand = result;

		cfgBuilder.endBranch();
		cfgBuilder.endBranch();
	}

	String currentClassName;
	@Override
	public void visit(NewObject n)
	{
		currentClassName = n.i.s;
		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new RecordAllocation(
				currentNamespace, n.i.s), currentOperand));
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
		if((result = currentMethod.getVar(e.s)) != null
				|| (result = currentMethod.getParam(e.s)) != null)
		{						
			if(result.type() instanceof IdentifierType)
				currentClassName = ((IdentifierType)result.type()).s;
			
			currentOperand = currentLocals.get(e.s);
			return;
		}		
		else if((result = currentClass.getVar(e.s)) != null)
		{
			if(result.type() instanceof IdentifierType)
				currentClassName = ((IdentifierType)result.type()).s;
			
			
			currentOperand = new RecordAccess("minijava", currentClass.getId(), new Identifier("this"), result.getMemoryOffset());
		}
		
	}
	
	@Override
	public void visit(syntaxtree.Identifier e)
	{
		
		Expression result = null;		
		if((result = currentLocals.get(e.s)) != null)
		{
			currentOperand = result;
			return;
		}
		else if(recordMap.get(currentClass.getId()) != null)
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
		cfgBuilder.addBranch(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));

		cfgBuilder.beginTrueBlock();
		i.s1.accept(this);

		cfgBuilder.beginFalseBlock();
		i.s2.accept(this);

		cfgBuilder.endBranch();
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
	
	@Override 
	public void visit(Equality e)
	{
		e.e1.accept(this);
		Expression leftOperand = currentOperand;
		e.e2.accept(this);
		Expression rightOperand = currentOperand;

		currentOperand = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new RelationalOp(RelationalOp.Op.EQ,
				leftOperand, rightOperand), currentOperand));		
	}	

	public void visit(False f)
	{
		currentOperand = new ir.ops.IntegerLiteral(0);
	}

	public void visit(True t)
	{
		currentOperand = new ir.ops.IntegerLiteral(1);
	}

	public void visit(Not n)
	{
		n.e.accept(this);
		Identifier result = cfgBuilder.getTemporary();
		cfgBuilder.addBranch(new RelationalOp(RelationalOp.Op.EQ, currentOperand,
				new ir.ops.IntegerLiteral(1)));
		cfgBuilder.beginFalseBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(1), result));
		cfgBuilder.beginTrueBlock();
		cfgBuilder
				.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), result));
		cfgBuilder.endBranch();
		currentOperand = result;
	}

	@Override
	public void visit(While n)
	{
		cfgBuilder.addLoop();
		cfgBuilder.beginTest();
		n.e.accept(this);
		cfgBuilder.setLoopTestResult(currentOperand);
		cfgBuilder.beginBody();
		n.s.accept(this);
		cfgBuilder.endLoop();
	}

	@Override
	public void visit(ForEach n)
	{
		// Initialize counter
		Identifier source = (Identifier)currentOperand;
		Identifier length = cfgBuilder.getTemporary();
		Identifier counter = cfgBuilder.getTemporary();
		Identifier testResult = cfgBuilder.getTemporary();
		cfgBuilder.addStatement(new Assignment(new ir.ops.ArrayLength(source), length));
		cfgBuilder.addStatement(new Assignment(new ir.ops.IntegerLiteral(0), counter));
		cfgBuilder.addLoop();		
		cfgBuilder.beginTest();
		cfgBuilder.addStatement(new Assignment(new RelationalOp(RelationalOp.Op.LT, counter, length), testResult));
		cfgBuilder.setLoopTestResult(testResult);
		cfgBuilder.beginBody();
		n.statement.accept(this);
		cfgBuilder.addStatement(new Assignment(new BinOp(Op.ADD, new ir.ops.IntegerLiteral(1), counter), currentOperand));
		cfgBuilder.endLoop();
	}
	
	@Override
	public void visit(ArrayLength l)
	{
		super.visit(l);
		currentOperand = new ir.ops.ArrayLength(currentOperand);
	}
}
