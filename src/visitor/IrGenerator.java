package visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import symboltable.Table;
import syntaxtree.IntegerLiteral;
import syntaxtree.MainClass;
import syntaxtree.Minus;
import syntaxtree.Plus;
import syntaxtree.Print;
import syntaxtree.PrintLn;
import syntaxtree.Times;
import ir.Alias;
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
	private Collection<Frame> frameList = new ArrayList<Frame>();
	
	public Collection<Frame> getFrameList()
	{
		return frameList;
	}
	
	@Override
	public void visit(Print p)
	{	
		List<Alias> parameters = new ArrayList<Alias>();
		for(int i = 0; i < p.e.size(); i++)
		{
			Temporary temp = currentFrame.getTempAllocator().GetTemporary();
			parameters.add(temp);
			temps.push(temp);
			p.e.elementAt(i).accept(this);
		}		
		
		currentBlock.addOperation(new SysCall("print", parameters, currentFrame.getTempAllocator().GetTemporary()));
	}
	
	public void visit(PrintLn p)
	{
		List<Alias> parameters = new ArrayList<Alias>();
		for(int i = 0; i < p.list.size(); i++)
		{
			Temporary temp = currentFrame.getTempAllocator().GetTemporary();
			parameters.add(temp);
			temps.push(temp);
			p.list.elementAt(i).accept(this);
		}		
		
		currentBlock.addOperation(new SysCall("println", parameters, currentFrame.getTempAllocator().GetTemporary()));		
	}
	
	public void visit(MainClass m)
	{
		currentBlock = new BasicBlock();
		m.s.accept(this);		
		
		currentFrame = new Frame("main", 0, 0, currentBlock );
		frameList.add(currentFrame);
	}
	
	public void visit(IntegerLiteral l)
	{
		
	}
	
	public void visit(Plus p)
	{
		
	}
	
	public void visit(Times t)
	{
		
	}
	
	public void visit (Minus m)
	{
		
	}
	
	
}
