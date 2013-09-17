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
    // print(5)
    static Stm print = new PrintStm(new LastExpList(new NumExp(5)));
    
    //#3 from web site
    // a := 5 + 3; print(a)
    static Stm assignPrint = new CompoundStm(new AssignStm("a", new OpExp(new NumExp(5), OpExp.Plus, new NumExp(3))),
            new PrintStm(new LastExpList(new IdExp("a"))));
    
    //#4 from web site
    //b := (print(10, 9, 8, (c := (print(11, 10, 9, 8, 7), 6), 5)), 3)
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
    
    //#5 My own "non-trivial" test
    // Demonstrates multiple assignments, including some "complex" on
    // a := 5; b := 8; c := (print(a, b, 12 + 3 - 11), a + b + 5); a := (d := a + b + c; b := d + b; print(a, b, c, d), 1); print(a)
    //See unit test for expected results.
    
    static Stm annoyingAndLongStm = new CompoundStm(
            new CompoundStm(
            new CompoundStm(
                    new AssignStm("a", new NumExp(5)),
                    new AssignStm("b", new NumExp(8))),
            new CompoundStm(
                    new AssignStm("c",
                            
                            new EseqExp(new PrintStm(
                                    new PairExpList(new IdExp("a"), new PairExpList(new IdExp("b"), new LastExpList(
                                            
                                    new OpExp(new OpExp(new NumExp(12), OpExp.Plus, new NumExp(3)), OpExp.Minus, new NumExp(11)))))), 
                                     
                                    new OpExp(new OpExp(new IdExp("a"), OpExp.Plus, new IdExp("b")), OpExp.Plus, new NumExp(5)))),
                     new AssignStm("a",
                             new EseqExp(
                                     new CompoundStm(new AssignStm("d",new OpExp(new OpExp(new IdExp("a"), OpExp.Plus, new IdExp("b")), OpExp.Plus, new IdExp("c"))),
                                             new CompoundStm(new AssignStm("b", new OpExp(new IdExp("d"), OpExp.Plus, new IdExp("b"))),
                                                             new PrintStm(new PairExpList(new IdExp("a"), new PairExpList(new IdExp("b"),
                                                                     new PairExpList(new IdExp("c"), new LastExpList(new IdExp("d")))))))),
                                                                     new NumExp(1))))),
                                                                     
                    new PrintStm(new LastExpList(new IdExp("a"))));
    
    
            
            
}
