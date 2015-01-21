Minijava Compiler
=================
Compile a class-specific variant of MiniJava to a variety of target formats. Largely follows the approaching from Andrew Appel's "Modern Compiler Implementation in Java." Supports x86, python 
and mips (SPIM) as target architecture.

Usage
=====
`usage: RamCompiler
 -f,--format <arg>   Output format. Valid options: x86, python, mips
 -h,--help           print usage
 -i,--input <arg>    input file.
 -o,--output <arg>   output file.`  
 
If the output file option is ommited the generated code will be written to standard output.
 
For example, to compile the "BinarySearchTree.ram" test program into x86 format:  
`$ java -cp target/minijava-compiler-1.jar compiler/RamCompiler -i programs/codegen/BinarySearchTree.ram -o test.s -f x86`  
  
To compile the same program for python and pipe the results to a python interpreter:
`$ java -cp target/minijava-compiler-1.jar compiler/RamCompiler -i programs/codegen/BinarySearchTree.ram -f python | python`  

Build
=====
The MiniJava compiler is a Maven project. With Maven and the JDK installed executate the following:  
`mvn clean install`  
