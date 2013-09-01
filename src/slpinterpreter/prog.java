package slpinterpreter;

class prog {

    static Stm prog =
            new CompoundStm(new AssignStm("a", new OpExp(new NumExp(5), OpExp.Plus,
            new NumExp(3))),
            new CompoundStm(new AssignStm("b",
            new EseqExp(new PrintStm(new PairExpList(new IdExp("a"),
            new LastExpList(new OpExp(new IdExp("a"), OpExp.Minus,
            new NumExp(1))))),
            new OpExp(new NumExp(10), OpExp.Times, new IdExp("a")))),
            new PrintStm(new LastExpList(new IdExp("b")))));
    
    //#1 from web site
    static Stm assign = new AssignStm("a", new NumExp(10));
    
    //#2 from web site
    static Stm print = new PrintStm(new LastExpList(new NumExp(5)));
    
    //#3 from web site
    static Stm assignPrint = new CompoundStm(new AssignStm("a", new OpExp(new NumExp(5), OpExp.Plus, new NumExp(3))),
            new PrintStm(new LastExpList(new IdExp("a"))));
    
    //#4 from web site
    // b := (print (10, 9, 8, (c := (print (11, 10, 9, 8, 7), 6), 5), 3)
    static Stm complexCompoundStm = new AssignStm("b",
            //EseqExp #1, side effect
            new EseqExp(new PrintStm(new PairExpList(new NumExp(10), new PairExpList(new NumExp(9), new PairExpList(new NumExp(8),
                    //EseqExp #2, still part of ExpList
                    new LastExpList(new EseqExp(new AssignStm("c",
                            //EseqExp #3
                            new EseqExp(new PrintStm(new PairExpList(new NumExp(11), new PairExpList(new NumExp(10), 
                                    new PairExpList(new NumExp(9), new PairExpList(new NumExp(8), new LastExpList(new NumExp(7))))))),
                                    //Exp of EsqExp#3
                                    new NumExp(6))),
                                //Exp of EsqExp #2
                                new NumExp(5))))
                            //Exp of EsqExp #1
                ))), new NumExp(3)));
}
