package ir.visitor;

import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;
import ir.ops.Label;
import ir.ops.RecordDeclaration;
import ir.ops.Statement;

import java.io.PrintStream;

public class IrWriter
{
	private IrPrintStream out;

	public IrWriter(PrintStream out)
	{
		this.out = new IrPrintStream(out);
	}
	
	public void printRecordDeclaration(RecordDeclaration r)
	{
		out.println(r.getNamespace() + "." + r.getId() + ":");
		out.indent();
		out.println(r.getFieldCount() + " fields");
		out.unindent();
		out.println("");
	}
	
	public void printFunction(FunctionDeclaration f)
	{
		out.println("");
		out.println("Function " + f.getNamespace() + "." + f.getId() + ":");
		out.println("Params: " + f.getParams().size());
		out.indent();
		for (Identifier param : f.getParams())
		{			
			out.println(param.getId());
		}
		out.unindent();		
		out.println("Locals: ");
		out.indent();
		for (Identifier value : f.getLocals())
		{			
			out.println(value.getId());
		}
		out.unindent();
		out.println("Temporaries: " + f.getTemporaries().size());
		out.indent();
		for (Identifier temp : f.getTemporaries())
		{			
			out.println(temp.getId());
		}		
		out.unindent();
		out.println("Begin:");
		out.indent();
		for(Statement statement : f.getStatements())
		{						
			if(statement instanceof Label)
			{
				out.unindent();
				out.println(statement.toString());
				out.indent();				
			}
			else
				out.println(statement.toString());
		}
		out.unindent();
		out.println("End " + f.getNamespace() + "." + f.getId() + ":");
	}
}
