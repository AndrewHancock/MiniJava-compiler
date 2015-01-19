package ir;

import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock
{
	BasicBlock parent;
	
	List<CodePoint> codePoints = new ArrayList<CodePoint>();	
	
	BasicBlock child;
	
	public void addOperation(Operation op)
	{
		CodePoint point = new CodePoint(op);
		codePoints.add(point);		
	}
	
	public List<CodePoint> getCodePoints()
	{
		return codePoints;
	}
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
		if(child != null)
			visitor.visit(child);
	}
	
	public BasicBlock getChild()
	{
		return child;
	}
	
	public void setChild(BasicBlock child)
	{
		this.child = child;
	}
	
	public BasicBlock getParent()
	{
		return parent;		
	}
	
	public void setParent(BasicBlock b)
	{
		this.parent = parent;
	}
}
