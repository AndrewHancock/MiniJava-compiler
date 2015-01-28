package visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import symboltable.Table;
import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.Assign;
import syntaxtree.Call;
import syntaxtree.ClassDeclSimple;
import syntaxtree.Formal;
import syntaxtree.If;
import syntaxtree.IntegerLiteral;
import syntaxtree.LessThanOrEqual;
import syntaxtree.MainClass;
import syntaxtree.MethodDecl;
import syntaxtree.Minus;
import syntaxtree.NewArray;
import syntaxtree.NewObject;
import syntaxtree.Or;
import syntaxtree.Plus;
import syntaxtree.Print;
import syntaxtree.PrintLn;
import syntaxtree.This;
import syntaxtree.Times;
import syntaxtree.VarDecl;
import ir.cfgraph.BasicBlock;
import ir.cfgraph.Block;
import ir.cfgraph.Conditional;
import ir.cfgraph.Frame;
import ir.ops.ArrayAssignment;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.DataType;
import ir.ops.Identifier;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.RelationalOp;
import ir.ops.Return;
import ir.ops.SysCall;
import ir.ops.Expression;
import ir.ops.BinOp.Op;
import ir.Temporary;

public class IrGenerator extends DepthFirstVisitor
{
	private Table table;
	
	public IrGenerator(Table symTable)
	{
		this.table = symTable;
	}
	
	private final String currentNamespace = "minijava";
	private Frame currentFrame;
	private BasicBlock currentBlock;	
	private Collection<Frame> frameList = new ArrayList<Frame>();
	private Collection<RecordDeclaration> recordList = new ArrayList<RecordDeclaration>();
	
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
		currentBlock = new BasicBlock();		
		
		currentFrame = new Frame("", "main", 0, 0, currentBlock );
		frameList.add(currentFrame);
		m.s.accept(this);				
	}
	
	@Override
	public void visit(Print p)
	{	
		List<Expression> parameters = new ArrayList<Expression>();
		for(int i = 0; i < p.e.size(); i++)
		{
			p.e.elementAt(i).accept(this);
			parameters.add(currentOperand);
		}		
		currentOperand = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new Assignment(new SysCall("print", parameters), currentOperand));
	}
	
	public void visit(PrintLn p)
	{
		List<Expression> parameters = new ArrayList<Expression>();
		for(int i = 0; i < p.list.size(); i++)
		{
			p.list.elementAt(i).accept(this);
			parameters.add(currentOperand);
		}		
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(new SysCall("println", parameters), currentOperand));		
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
		
		Identifier dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new Assignment(new BinOp(Op.ADD, leftOperand, rightOperand), dest));		
		currentOperand = dest;
	}
	
	public void visit(Times t)
	{
		t.e1.accept(this);
		Expression leftOperand = currentOperand;
		t.e2.accept(this);
		Expression rightOperand = currentOperand;
		
		Identifier dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new Assignment(new BinOp(Op.MULT, leftOperand, rightOperand), dest));		
		currentOperand = dest;
	}
	
	public void visit (Minus m)
	{
		m.e1.accept(this);
		Expression leftOperand = currentOperand;
		m.e2.accept(this);
		Expression rightOperand = currentOperand;
		
		Identifier dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new Assignment(new BinOp(Op.SUBTRACT, leftOperand, rightOperand), dest));		
		currentOperand = dest;	
	}
	
	
	private String currentClassName; 
	public void visit (ClassDeclSimple c)
	{
		currentClassName = c.i.s;
		RecordDeclaration declaration = new RecordDeclaration(currentNamespace, currentClassName);
		for(int i = 0; i < c.vl.size(); i ++)
			declaration.addField(DataType.INT);
		recordList.add(declaration);	
		
		for(int i = 0; i < c.ml.size(); i++)
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
		currentBlock.addOperation(new Assignment(currentOperand, new Identifier(a.i.s)));		
	}

	@Override
	public void visit(ArrayAssign a)
	{
		a.e2.accept(this);
		Expression sourceOperand = currentOperand;
		a.e1.accept(this);
		Expression index = currentOperand;
		currentBlock.addOperation(new ArrayAssignment(sourceOperand, new Identifier(a.i.s), index));		
	}
	
	@Override
	public void visit(MethodDecl d)
	{		
		currentBlock = new BasicBlock();
		currentFrame = new Frame(currentNamespace, d.i.s, d.fl.size(), d.vl.size(), currentBlock );
		frameList.add(currentFrame);
		
		currentLocals.clear();
		
		// For any method declaration, "this" is implicit
		currentFrame.getParams().add(new Identifier("this"));
		currentLocals.put("this", new Identifier("this"));		
		
		for(int i = 0; i < d.vl.size(); i++)
		{			
			currentFrame.getLocals().add(new Identifier(d.vl.elementAt(i).i.s));
			d.vl.elementAt(i).accept(this);
		}
		
		for(int i = 0; i < d.fl.size(); i++)
		{
			currentFrame.getParams().add(new Identifier(d.fl.elementAt(i).i.s));
			d.fl.elementAt(i).accept(this);
		}		
		
		for(int i = 0; i < d.sl.size(); i++)
			d.sl.elementAt(i).accept(this);
		
		d.e.accept(this);
		currentBlock.addOperation(new Return(currentOperand));
	}
	
	@Override 
	public void visit(Call c)
	{
		ArrayList<Expression> params = new ArrayList<Expression>(c.el.size() + 1);		
		
		c.e.accept(this);
		params.add(currentOperand);
		
		for(int i = 0; i < c.el.size(); i ++)
		{
			c.el.elementAt(i).accept(this);
			params.add(currentOperand);
		}
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		ir.ops.Call call = new ir.ops.Call(c.i.s, params);
		currentBlock.addOperation(new Assignment(call, currentOperand));
	}
	
	@Override 
	public void visit(And a)
	{
		a.e1.accept(this);
		Expression src1 = currentOperand;
		a.e2.accept(this);
		Expression src2 = currentOperand;
		
		Identifier dest = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(new BinOp(Op.AND, src1, src2), dest));
	}
	
	@Override 
	public void visit(Or a)
	{
		a.e1.accept(this);
		Expression src1 = currentOperand;
		a.e2.accept(this);
		Expression src2 = currentOperand;
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(currentOperand, new BinOp(Op.OR, src1, src2)));
	}

	@Override
	public void visit(NewObject n)
	{	
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(new RecordAllocation(currentNamespace, n.i.s), currentOperand));		
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
		currentOperand = currentLocals.get(e.s);
	}
	
	public void visit(This t)
	{
		currentOperand = currentLocals.get("this");
	}
	
	public void visit(If i)
	{
		i.e.accept(this);
		
		Conditional block = new Conditional(currentBlock, currentOperand);
		
		currentBlock = block.getTrueBlock();
		i.s1.accept(this);
		currentBlock = block.getFalseBlock();
		i.s2.accept(this);
		// Hack - A "Conditional" is not the same as a "BasicBlock"
		currentBlock = (BasicBlock)block.getSuccessor();
	}
	
	public void visit(LessThanOrEqual l)
	{
		l.e1.accept(this);
		Expression leftOperand = currentOperand;
		l.e2.accept(this);
		Expression rightOperand = currentOperand;
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(new RelationalOp(RelationalOp.Op.LTE, leftOperand, rightOperand), currentOperand));
	}
	
	
	
}
