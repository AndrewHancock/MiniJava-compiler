package ir.visitor;

import java.io.PrintStream;

public class IrPrintStream
{
	private PrintStream ps;

	public IrPrintStream(PrintStream ps)
	{
		this.ps = ps;
	}

	int indentLevel;

	public void indent()
	{		
		indentLevel++;		
	}

	public void unindent()
	{
		indentLevel--;
	}
	
	private void printIndent()
	{
		for (int i = 0; i < indentLevel; i++)
			ps.print("    ");
		startOfLine = false;
	}
	
	boolean startOfLine; 

	public void print(int i)
	{
		if(startOfLine)
		{
			printIndent();
			startOfLine = false;
		}
		ps.print(i);
		
	}

	public void print(String s)
	{
		if(startOfLine)
		{
			printIndent();
			startOfLine = false;
		}		
		ps.print(s);		
	}

	public void println(String s)
	{
		// TODO Auto-generated method stub
		ps.println(s);
		startOfLine = true;
		
	}

	public void println(int i)
	{
		ps.print(i);
		println("");
	}
}
