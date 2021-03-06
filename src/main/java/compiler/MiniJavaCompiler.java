package compiler;

import ir.backend.X64CodeGenerator;
import ir.ops.FunctionDeclaration;
import ir.ops.RecordDeclaration;
import ir.visitor.IrVisitor;
import ir.visitor.IrWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import symboltable.Table;
import syntaxtree.Program;
import syntaxtree.visitor.BuildSymbolTableVisitor;
import syntaxtree.visitor.IrGenerator;
import syntaxtree.visitor.MipsCodeGenerator;
import syntaxtree.visitor.PythonVisitor;
import syntaxtree.visitor.TypeCheckVisitor;
import syntaxtree.visitor.Visitor;
import syntaxtree.visitor.X86CodeGenerator;
import frontend.generated.RamParser;

public class MiniJavaCompiler
{

	public static Options getCliOptions()
	{
		Options options = new Options();
		options.addOption(new Option("o", "output", true, "output path"));
		options.addOption(new Option("i", "input", true, "input path"));
		options.addOption(new Option("f", "format", true,
				"Output format. Valid options: x64, x86, python, mips"));
		options.addOption(new Option("h", "help", false, "print usage"));
		options.addOption(new Option("k", "reg-limit", true, "maximum number of registers used by allocator"));

		return options;

	}

	public static Visitor getVisitorForFormatString(String format, PrintStream ps,
			Table symTable)
	{
		format = format.toUpperCase();
		if (format.equals("PYTHON"))
			return new PythonVisitor(ps, symTable);
		else if (format.equals("MIPS"))
			return new MipsCodeGenerator(ps, symTable);
		else if (format.equals("X86"))
			return new X86CodeGenerator(ps, symTable);
		else
			throw new IllegalArgumentException(
					"Invalid output format specified. Valid options are: x86, python, mips");

	}

	public static void main(String[] args) throws ParseException,
			frontend.generated.ParseException, IOException
	{
		BasicParser parser = new BasicParser();
		Options cliOptions = getCliOptions();
		CommandLine options = parser.parse(cliOptions, args);

		if (options.hasOption("h"))
		{
			new HelpFormatter().printHelp("MiniJavaCompiler", cliOptions);
			return;
		}

		Program root = null;

		InputStream in = System.in;
		if (options.hasOption('i'))
			in = new FileInputStream(new File(options.getOptionValue('i')));
		try
		{
			// Parser constructor must be invoked for side-effect
			new frontend.generated.RamParser(in);
			root = RamParser.Goal();
		}
		finally
		{
			if (in != System.in)
				in.close();
		}

		// build symbol table
		BuildSymbolTableVisitor symbolTableVisitor = new BuildSymbolTableVisitor();
		root.accept(symbolTableVisitor);

		// perform type checking
		TypeCheckVisitor typeChecker = new TypeCheckVisitor(
				symbolTableVisitor.getSymTab());
		root.accept(typeChecker);

		if (typeChecker.getErrorMsg().getHasErrors())
		{
			System.out.println("Error in front-end. Exiting.");
			System.exit(1);
		}
		PrintStream ps = System.out;
		if (options.hasOption('o'))
			ps = new PrintStream(new java.io.FileOutputStream(
					options.getOptionValue('o')));

		IrGenerator irGenerator = new IrGenerator(symbolTableVisitor.getSymTab());
		root.accept(irGenerator);
		
		if(options.hasOption('k') && !options.getOptionValue('f').toUpperCase().equals("X64"))
		{
			System.out.println("'k' paramter available only for X64 output.");
			System.exit(1);
		}

		if (options.getOptionValue('f').toUpperCase().equals("IR"))
		{
			IrWriter irWriter = new IrWriter(ps);

			for (RecordDeclaration recDec : irGenerator.getRecordList())
			{
				irWriter.printRecordDeclaration(recDec);
			}
			for (FunctionDeclaration funcDec : irGenerator.getFrameList())
			{
				irWriter.printFunction(funcDec);
			}
		}
		else if (options.getOptionValue('f').toUpperCase().equals("X64"))
		{
			compileX64(irGenerator.getRecordList(), irGenerator.getFrameList(), ps, options );
		}
		else
		{
			Visitor visitor = getVisitorForFormatString(options.getOptionValue('f'), ps, symbolTableVisitor.getSymTab());
			root.accept(visitor);
		}
		ps.close();
	}

	private static void compileX64(Collection<RecordDeclaration> records, Collection<FunctionDeclaration> functions, PrintStream ps, CommandLine options)
	{

		IrVisitor codeGenerator;
		if(options.hasOption("k"))
			codeGenerator = new X64CodeGenerator(ps, Integer.parseInt((options.getOptionValue("k"))));
		else			
			codeGenerator = new X64CodeGenerator(ps );
		for (RecordDeclaration recDec : records)
		{
			recDec.accept(codeGenerator);
		}

		for (FunctionDeclaration frameDec : functions)
		{
			frameDec.accept(codeGenerator);
		}
	}
}
