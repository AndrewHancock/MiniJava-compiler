package slpinterpreter;

public class TestOutput implements Output
{
    private String output = new String();
    
    public TestOutput()
    {
        
    }
    
    public void print(String string)
    {
        output += string;
    }
    
    public void printNewLine()
    {
        output += "\n";
    }
    
    public String getOutput()
    {
        return output;
    }
}
