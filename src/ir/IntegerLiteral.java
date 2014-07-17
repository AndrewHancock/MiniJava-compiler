package ir;

import ir.visitor.IrVisitor;

public class IntegerLiteral implements Value
{
	int value;

	public IntegerLiteral(int value) { this.value = value; }	
	public int getValue() { return value; }
	
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
