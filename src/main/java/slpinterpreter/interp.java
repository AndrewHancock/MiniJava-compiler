package slpinterpreter;

class interp 
{
    public static void main(String args[]) throws java.io.IOException 
    {
        System.out.println(Integer.toString(prog.prog.maxargs()));
        
        //Our initial table contains no mappings, so we pass null
        prog.prog.evaluate(null);

        System.out.println(prog.prog.toString());
    }
}
