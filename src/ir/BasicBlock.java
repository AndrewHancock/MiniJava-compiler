package ir;

import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock
{
	BasicBlock parent;
	
	List<CodePoint> codePoints = new ArrayList<CodePoint>();	
	
	List<BasicBlock> children = new ArrayList<BasicBlock>();
	
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
	}
}
