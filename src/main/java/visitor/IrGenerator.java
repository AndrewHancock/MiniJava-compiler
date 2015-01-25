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
import syntaxtree.IntegerLiteral;
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
import ir.cfgraph.ConditionalBasicBlock;
import ir.ops.ArrayAssignment;
import ir.ops.Assignment;
import ir.ops.BinOp;
import ir.ops.DataType;
import ir.ops.Frame;
import ir.ops.Identifier;
import ir.ops.IdentifierExp;
import ir.ops.RecordAllocation;
import ir.ops.RecordDeclaration;
import ir.ops.SysCall;
import ir.ops.Value;
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
	private Stack<Temporary> temps = new Stack<Temporary>();
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
		List<Value> parameters = new ArrayList<Value>();
		for(int i = 0; i < p.e.size(); i++)
		{
			p.e.elementAt(i).accept(this);
			parameters.add(currentOperand);
		}		
		currentOperand = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new Assignment(new SysCall("print", parameters, currentFrame.getTempAllocator().GetTemporary()), currentOperand));
	}
	
	public void visit(PrintLn p)
	{
		List<Value> parameters = new ArrayList<Value>();
		for(int i = 0; i < p.list.size(); i++)
		{
			p.list.elementAt(i).accept(this);
		}		
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(new SysCall("println", parameters, currentFrame.getTempAllocator().GetTemporary()), currentOperand));		
	}
	
	Value currentOperand = null;
	
	@Override
	public void visit(IntegerLiteral l)
	{
		currentOperand = new ir.ops.IntegerLiteral(l.i);
	}	
	
	
	public void visit(Plus p)
	{		
		p.e1.accept(this);
		Value leftOperand = currentOperand;
		p.e2.accept(this);
		Value rightOperand = currentOperand;
		
		IdentifierExp dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new BinOp(Op.ADD, dest, leftOperand, rightOperand));		
		currentOperand = dest;
	}
	
	public void visit(Times t)
	{
		t.e1.accept(this);
		Value leftOperand = currentOperand;
		t.e2.accept(this);
		Value rightOperand = currentOperand;
		
		IdentifierExp dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new BinOp(Op.MULT, dest, leftOperand, rightOperand));		
		currentOperand = dest;
	}
	
	public void visit (Minus m)
	{
		m.e1.accept(this);
		Value leftOperand = currentOperand;
		m.e2.accept(this);
		Value rightOperand = currentOperand;
		
		IdentifierExp dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new BinOp(Op.SUBTRACT, dest, leftOperand, rightOperand));		
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
		Value sourceOperand = currentOperand;
		a.e1.accept(this);
		Value index = currentOperand;
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
		currentFrame.getParams().add(new IdentifierExp("this"));
		currentLocals.put("this", new IdentifierExp("this"));		
		
		for(int i = 0; i < d.vl.size(); i++)
		{			
			currentFrame.getLocals().add(new IdentifierExp(d.vl.elementAt(i).i.s));
			d.vl.elementAt(i).accept(this);
		}
		
		for(int i = 0; i < d.fl.size(); i++)
		{
			currentFrame.getParams().add(new IdentifierExp(d.fl.elementAt(i).i.s));
			d.fl.elementAt(i).accept(this);
		}		
		
		for(int i = 0; i < d.sl.size(); i++)
			d.sl.elementAt(i).accept(this);
		
		d.e.accept(this);
	}
	
	@Override 
	public void visit(Call c)
	{
		ArrayList<Value> params = new ArrayList<Value>(c.el.size() + 1);		
		
		c.e.accept(this);
		params.add(currentOperand);
		
		for(int i = 0; i < c.el.size(); i ++)
		{
			c.el.elementAt(i).accept(this);
			params.add(currentOperand);
		}
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		ir.ops.Call call = new ir.ops.Call(c.i.s, params, currentOperand);
		currentBlock.addOperation(new Assignment(call, currentOperand));
	}
	
	@Override 
	public void visit(And a)
	{
		a.e1.accept(this);
		Value src1 = currentOperand;
		a.e2.accept(this);
		Value src2 = currentOperand;
		
		IdentifierExp temp = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new BinOp(Op.AND, temp, src1, src2));
	}
	
	@Override 
	public void visit(Or a)
	{
		a.e1.accept(this);
		Value src1 = currentOperand;
		a.e2.accept(this);
		Value src2 = currentOperand;
		
		BasicBlock condition = new BasicBlock();
		currentBlock.addOperation(new BinOp(Op.OR, currentFrame.getTempAllocator().GetTemporary(), src1, src2));
		
		BasicBlock trueBlock = new BasicBlock();
		trueBlock.addOperation(new Assignment(currentOperand, new ir.ops.IntegerLiteral(1)));
		
		BasicBlock falseBlock = new BasicBlock();
		trueBlock.addOperation(new Assignment(currentOperand, new ir.ops.IntegerLiteral(0)));
		
		currentBlock.setChild(new ConditionalBasicBlock(condition,  trueBlock, falseBlock ));
		currentBlock.getChild().setParent(currentBlock);
		currentBlock = currentBlock.getChild();
	}

	@Override
	public void visit(NewObject n)
	{	
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(new RecordAllocation(currentNamespace, n.i.s), currentOperand));		
	}
	
	HashMap<String, Value> currentLocals = new HashMap<String, Value>(); 
	public void visit(VarDecl v)
	{
		currentLocals.put(v.i.s, new IdentifierExp(v.i.s));		
	}
	
	public void visit(Formal f)
	{
		currentLocals.put(f.i.s, new IdentifierExp(f.i.s));
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
	
}
