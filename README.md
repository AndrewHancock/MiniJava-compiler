Minijava Compiler
=================
A compiler for a variant of "MiniJava." Supports x86 (AT&T Syntax), python and MIPS (SPIM) as target architecture.

Usage
=====
The compiler is invoked from the command line. 

	usage: RamCompiler
	 -f,--format <arg>   Output format. Valid options: x86, python, mips
	 -h,--help           print usage
	 -i,--input <arg>    input file.
	 -o,--output <arg>   output file.  
 
If the output file option is ommited the generated code will be written to standard output.
 
For example, to compile the "BinarySearchTree.ram" test program into x86 format:  
`$ java -cp target/minijava-compiler-1.jar compiler/RamCompiler -i programs/codegen/BinarySearchTree.ram -o test.s -f x86`  
  
To compile the same program for python and pipe the results to a python interpreter:
`$ java -cp target/minijava-compiler-1.jar compiler/RamCompiler -i programs/codegen/BinarySearchTree.ram -f python | python`  

Build
=====
The MiniJava compiler is a Maven project. With Maven and the JDK installed executate the following:  
`mvn clean install`  
For some IDEs, such as Eclipse, you may need to copy the generated source code from the directory target/generated-sources/javacc/frontend/generated
to src/main/java/frontend/generated when re-generating the grammar sources.
