package slpinterpreter;

public interface Output
{
    void print(String string);
    void printNewLine();
}

class ConsoleOutput implements Output
{
    public void print(String string)
    {
        System.out.print(string);
    }
    
    public void printNewLine()
    {
        System.out.println();
    }
}

