package slpinterpreter;

import static org.junit.Assert.*;
import org.junit.Test;

public class FirstProgramTest
{

    @Test
    public void testEvaluate()
    {
        Stm stmt = prog.prog;
        TestOutput output = new TestOutput();
        PrintStm.output = output;
        
        stmt.evaluate(new IdNumHashMap());
        
        assertEquals("Unexpected output", "8 7\n80\n", output.getOutput());
    }

}
