package visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import symboltable.Table;
import syntaxtree.ArrayAssign;
import syntaxtree.Assign;
import syntaxtree.ClassDecl;
import syntaxtree.ClassDeclSimple;
import syntaxtree.IntegerLiteral;
import syntaxtree.MainClass;
import syntaxtree.Minus;
import syntaxtree.NewArray;
import syntaxtree.Plus;
import syntaxtree.Print;
import syntaxtree.PrintLn;
import syntaxtree.Times;
import ir.ArrayAssignment;
import ir.Assignment;
import ir.BinOp;
import ir.BinOp.Op;
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
		
		currentBlock.addOperation(new SysCall("print", parameters, currentFrame.getTempAllocator().GetTemporary()));
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
		
		currentBlock.addOperation(new SysCall("println", parameters, currentFrame.getTempAllocator().GetTemporary()));		
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
	
	public void visit (ClassDeclSimple c)
	{
		String namespace = "minijava";
		RecordDeclaration declaration = new RecordDeclaration(namespace, c.i.s);
		for(int i = 0; i < c.ml.size(); i ++)
			declaration.addField(DataType.INT);
		declarationList.add(declaration);
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
		currentBlock.addOperation(new Assignment(new Identifier(a.i.s), currentOperand));		
	}

	@Override
	public void visit(ArrayAssign a)
	{
		a.e2.accept(this);
		Value sourceOperand = currentOperand;
		a.e1.accept(this);
		Value index = currentOperand;
		currentBlock.addOperation(new ArrayAssignment(new Identifier(a.i.s), sourceOperand, index));		
	}	
}
