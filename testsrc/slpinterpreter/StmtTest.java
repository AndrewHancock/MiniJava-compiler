package slpinterpreter;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class StmtTest
{
    private TestOutput output;    
    
    @Before
    public void setup()
    {
        output = new TestOutput();         
        PrintStm.output = output;
    }
    
    @Test 
    // a := 5+3; b := (print(a, a-1), 10*a); print(b)
    public void testFirstProg()
    {
        Table table = prog.prog.evaluate(null);
        
        assertEquals("Unexpected output", "8 7\n80\n", output.getOutput());
        assertEquals("Invalid id->num mapping", 8, table.lookup("a"));
        assertEquals("invalid id->num mapping", 80, table.lookup("b"));
        assertEquals("Invalid maxargs()", 2, prog.prog.maxargs());
        assertEquals("Unexpected assignments", 2, getTableCount(table));
    }
    
    @Test
    // a := 10
    public void testAssign()
    {
        Table table = prog.assign.evaluate(null);
        
        //Ensure no output
        assertEquals("Unexpected output", "".trim(), output.getOutput().trim());
        assertEquals("Inavlid id->num mapping", 10, table.lookup("a"));
        assertEquals("Invalid maxargs()", 0, prog.assign.maxargs());
        assertEquals("Unexpected assignments", 1, getTableCount(table));
    }
    
    @Test
    //print(5)
    public void testPrint()
    {
        Table table = prog.print.evaluate(null);
        
        assertEquals("Unexpected output", "5\n", output.getOutput());  
        assertEquals("Invalid maxargs()", 1, prog.print.maxargs());
        assertNull("Table is not null", table);
    }
    
    @Test
    // a := 5 + 3; print(a)
    public void testAssignPrint()
    {
        Table table = prog.assignPrint.evaluate(null);
        
        assertEquals("Unexpected output", "8\n", output.getOutput());
        assertEquals("Invalid id->num mapping", 8, table.lookup("a"));
        assertEquals("Invalid maxargs()", 1, prog.assignPrint.maxargs());
        assertEquals("Unexpected assignments", 1, getTableCount(table));
    }
        
    @Test
    //b := (print (10, 9, 8, c := (print (11, 10, 9, 8, 7), 6)), 3)
    public void testComplexExample()
    {
        
        Table table = prog.complexCompoundStm.evaluate(null);
        
        assertEquals("Unexpected output", "10 9 8 11 10 9 8 7\n5\n", output.getOutput());
        assertEquals("Invalid id->num mapping", 3, table.lookup("b"));
        assertEquals("invalid id->num mapping", 6, table.lookup("c"));
        assertEquals("Invalid maxargs()", 5, prog.complexCompoundStm.maxargs());
        assertEquals("Unexpected assignments", 2, getTableCount(table));
    }
    
    // This method should not be called on an empty (null) table.
    // It is used to verify that no unexpected assignments occurred. 
    // assertNull should be used if you want to verify that the table is null.
    private int getTableCount(Table table)
    {
        int count = 0;
        Table currentTable = table;
        do        
            count++;
        while((currentTable = currentTable.getTail()) != null);
        
        return count;
    }
}
