package slpinterpreter;

class interp 
{
    public static void main(String args[]) throws java.io.IOException 
    {
        System.out.println(Integer.toString(prog.prog.maxargs()));
        prog.prog.evaluate(new IdNumHashMap());
    }
}
