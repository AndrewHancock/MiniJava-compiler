package slpinterpreter;

class interp {

    static void interp(Stm s) 
    { 
    	s.evaluate(new IdNumHashMap());
    }

    static int maxargs(Stm s) 
    { 
    	/* you write this part */
        return 0;
    }

    public static void main(String args[]) throws java.io.IOException 
    {
        System.out.println(maxargs(prog.prog));
        interp(prog.prog);
    }
}
