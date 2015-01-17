package visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import symboltable.Table;
import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.Assign;
import syntaxtree.Call;
import syntaxtree.ClassDecl;
import syntaxtree.ClassDeclSimple;
import syntaxtree.IntegerLiteral;
import syntaxtree.MainClass;
import syntaxtree.MethodDecl;
import syntaxtree.Minus;
import syntaxtree.NewArray;
import syntaxtree.Or;
import syntaxtree.Plus;
import syntaxtree.Print;
import syntaxtree.PrintLn;
import syntaxtree.Times;
import ir.ArrayAssignment;
import ir.Assignment;
import ir.BinOp;
import ir.BinOp.Op;
import ir.ConditionalBasicBlock;
import ir.DataType;
import ir.Declaration;
import ir.Identifier;
import ir.RecordDeclaration;
import ir.Value;
import ir.BasicBlock;
import ir.Frame;
import ir.SysCall;
import ir.Temporary;

public class IrGenerator extends DepthFirstVisitor
{
	private Table table;
	
	public IrGenerator(Table symTable)
	{
		this.table = symTable;
	}
	
	private Frame currentFrame;
	private BasicBlock currentBlock;
	private Stack<Temporary> temps = new Stack<Temporary>();
	private Collection<Declaration> declarationList = new ArrayList<Declaration>();
	
	public Collection<Declaration> getDeclarationList()
	{
		return declarationList;
	}	
	
	public void visit(MainClass m)
	{
		currentBlock = new BasicBlock();		
		
		currentFrame = new Frame("", "main", 0, 0, currentBlock );
		declarationList.add(currentFrame);
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
			Temporary temp = currentFrame.getTempAllocator().GetTemporary();
			parameters.add(temp);
			temps.push(temp);
			p.list.elementAt(i).accept(this);
		}		
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		currentBlock.addOperation(new Assignment(new SysCall("println", parameters, currentFrame.getTempAllocator().GetTemporary()), currentOperand));		
	}
	
	Value currentOperand = null;
	
	@Override
	public void visit(IntegerLiteral l)
	{
		currentOperand = new ir.IntegerLiteral(l.i);
	}	
	
	
	public void visit(Plus p)
	{		
		p.e1.accept(this);
		Value leftOperand = currentOperand;
		p.e2.accept(this);
		Value rightOperand = currentOperand;
		
		Temporary dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new BinOp(Op.ADD, dest, leftOperand, rightOperand));		
		currentOperand = dest;
	}
	
	public void visit(Times t)
	{
		t.e1.accept(this);
		Value leftOperand = currentOperand;
		t.e2.accept(this);
		Value rightOperand = currentOperand;
		
		Temporary dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new BinOp(Op.MULT, dest, leftOperand, rightOperand));		
		currentOperand = dest;
	}
	
	public void visit (Minus m)
	{
		m.e1.accept(this);
		Value leftOperand = currentOperand;
		m.e2.accept(this);
		Value rightOperand = currentOperand;
		
		Temporary dest = currentFrame.getTempAllocator().GetTemporary(); 
		currentBlock.addOperation(new BinOp(Op.SUBTRACT, dest, leftOperand, rightOperand));		
		currentOperand = dest;	
	}
	
	private String currentNamespace;
	private String currentClassName; 
	public void visit (ClassDeclSimple c)
	{
		currentNamespace = "minijava";
		currentClassName = c.i.s;
		RecordDeclaration declaration = new RecordDeclaration(currentNamespace, currentClassName);
		for(int i = 0; i < c.vl.size(); i ++)
			declaration.addField(DataType.INT);
		declarationList.add(declaration);	
		
		for(int i = 0; i < c.ml.size(); i++)
		{
			c.ml.elementAt(i).accept(this);
		}
		
	}
	
	@Override
	public void visit(NewArray a)
	{
		a.e.accept(this);		
		ir.NewArray newArray = new ir.NewArray(DataType.INT, currentOperand);
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
		declarationList.add(currentFrame);
		
		for(int i = 0; i < d.vl.size(); i++)
		{
			currentFrame.getLocals().add(new Identifier(d.vl.elementAt(i).i.s));			
		}
		
		for(int i = 0; i < d.fl.size(); i++)
		{
			currentFrame.getLocals().add(new Identifier(d.fl.elementAt(i).i.s));			
		}		
		
		for(int i = 0; i < d.sl.size(); i++)
			d.sl.elementAt(i).accept(this);
		
		d.e.accept(this);
	}
	
	@Override 
	public void visit(Call c)
	{
		ArrayList<Value> params = new ArrayList<Value>(c.el.size());
		for(int i = 0; i < c.el.size(); i ++)
		{
			c.el.elementAt(i).accept(this);
			params.add(currentOperand);
		}
		
		currentOperand = currentFrame.getTempAllocator().GetTemporary();
		ir.Call call = new ir.Call(c.i.s, params, currentOperand);
		currentBlock.addOperation(new Assignment(call, currentOperand));
	}
	
	@Override 
	public void visit(And a)
	{
		a.e1.accept(this);
		Value src1 = currentOperand;
		a.e2.accept(this);
		Value src2 = currentOperand;
		
		Temporary temp = currentFrame.getTempAllocator().GetTemporary();
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
		trueBlock.addOperation(new Assignment(currentOperand, new ir.IntegerLiteral(1)));
		
		BasicBlock falseBlock = new BasicBlock();
		trueBlock.addOperation(new Assignment(currentOperand, new ir.IntegerLiteral(0)));
		
		currentBlock.setChild(new ConditionalBasicBlock(condition,  trueBlock, falseBlock ));
		currentBlock.getChild().setParent(currentBlock);
		currentBlock = currentBlock.getChild();		
		
		
	}
}
