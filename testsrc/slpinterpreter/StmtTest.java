package slpinterpreter;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class StmtTest
{
    private TestOutput output;
    private IdNumMap idNumMap;
    
    @Before
    public void setup()
    {
        output = new TestOutput();
        idNumMap =new IdNumHashMap(); 
        PrintStm.output = output;
    }
    
    @Test 
    // a := 5+3; b := (print(a, a-1), 10*a); print(b)
    public void testFirstProg()
    {
        prog.prog.evaluate(idNumMap);
        
        assertEquals("Unexpected output", "8 7\n80\n", output.getOutput());
        assertEquals("Invalid id->num mapping", 8, idNumMap.lookup("a"));
        assertEquals("invalid id->num mapping", 80, idNumMap.lookup("b"));
    }
    
    @Test
    // a := 10
    public void testAssign()
    {
        prog.assign.evaluate(idNumMap);
        
        //Ensure no output
        assertEquals("Unexpected output", "".trim(), output.getOutput().trim());
        assertEquals("Inalid id-num mapping", 10, idNumMap.lookup("a"));
    }
    
    @Test
    //print(5)
    public void testPrint()
    {
        prog.print.evaluate(idNumMap);
        
        assertEquals("Unexpected output", "5\n", output.getOutput());        
    }
    
    @Test
    // a := 5 + 3; print(a)
    public void testAssignPrint()
    {
        prog.assignPrint.evaluate(idNumMap);
        
        assertEquals("Unexpected output", "8\n", output.getOutput());
        assertEquals("Invalid id->num mapping", 8, idNumMap.lookup("a"));
    }
        
    @Test
    //b := (print (10, 9, 8, c := (print (11, 10, 9, 8, 7), 6)), 3)
    public void testComplexExample()
    {
        
        prog.complexCompoundStm.evaluate(idNumMap);
        
        assertEquals("Unexpected output", "10 9 8 11 10 9 8 7\n5\n", output.getOutput());
        assertEquals("Invalid id->num mapping", 3, idNumMap.lookup("b"));
        assertEquals("invalid id->num mapping", 6, idNumMap.lookup("c"));
    }
}
