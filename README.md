Minijava Compiler
=================
A compiler for a variant of "MiniJava." Supports x86 (AT&T Syntax), python and MIPS (SPIM) as target architecture.

Usage
=====
The compiler is invoked from the command line. 

usage: RamCompiler
 -f,--format <arg>   Output format. Valid options: x86, python, mips
 -h,--help           print usage
 -i,--input <arg>    input path
 -o,--output <arg>   output path

If the output file option is ommited the generated code will be written to standard output.
 
For example, to compile the "BinarySearchTree.ram" test program into x86 format:  
`$ java -cp target/minijava-compiler-1.jar compiler/RamCompiler -i programs/codegen/BinarySearchTree.ram -o test.s -f x86`  
  
To compile the same program for python and pipe the results to a python interpreter:
`$ java -cp target/minijava-compiler-1.jar compiler/RamCompiler -i programs/codegen/BinarySearchTree.ram -f python | python`  

Output Format
=============
The compiler is able to output 32-bit x86 assembly language, Python, MIPS (SPIM) and intermediate representation. 

When targetting x86, the compiler first generates an intermediate representation and then translates that the x86 assembly language. The ir format outputs the intermediate representation in a text format and is useful for debugging purposes.

Python and MIPs are generated directly from the Abstract Syntax Tree of the input source and do not use the intermediate representation. 

Build
=====
The MiniJava compiler is a Maven project. With Maven and the JDK installed execute the following:  
`mvn clean install`  

For some IDEs, such as Eclipse, you may need to copy the generated source code from the directory target/generated-sources/javacc/frontend/generated
to src/main/java/frontend/generated when re-generating the grammar sources.
