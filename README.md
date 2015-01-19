Minijava Compiler
=================
Compile a class-specific variant of MiniJava to x86. Largely follows the approaching from Andrew Appel's "Modern Compiler Implementation in Java." 

Usage
=====
The compiler is distributed as a Maven project. With Maven and the JDK properly configurated, simply execute the following from the root folder of the project:  
`java -cp target/minijava-compiler-1.jar compiler/RamCompiler `  

This will produce a file of the same name with an additional .s extension. This file can be assembled using GNU asm. Gcc can be used to assembl the output, for example (on Windows with MinGW):  
'gcc -o BinarySearch.exe programs/codegen/BinarySearchTree.ram.s`

Build
=====
The MiniJava compiler is a Maven project. With Maven and the JDK installed, simply executate the following:  
`mvn clean install`  
