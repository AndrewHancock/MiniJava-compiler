package slpinterpreter;

interface Stm 
{
	void evaluate();
}

class CompoundStm implements Stm 
{
   Stm stm1, stm2;
   CompoundStm(Stm s1, Stm s2) 
   {
	   stm1=s1; 
	   stm2=s2;
   }
   
   public void evaluate()
   {
	stm1.evaluate();
	stm2.evaluate();
   }
}

class AssignStm implements Stm 
{
   String id;
   Exp exp;
   AssignStm(String i, Exp e) 
   {
	   id=i;
	   exp=e;
   }
   
   public void evaluate()
   {
	   throw new RuntimeException("Not implemented.");
   }   
}

class PrintStm implements Stm 
{
   ExpList exps;
   PrintStm(ExpList e) 
   {
	   exps=e;
   }
   
   public void evaluate()
   {
	   
   }   
}

interface Exp 
{
	int evaluate();
}

class IdExp implements Exp 
{
   String id;
   IdExp(String i) 
   {
	   id=i;
   }
   
   public int evaluate()
   {
	   throw new RuntimeException("Not implemented");	   
   }
}

class NumExp implements Exp 
{
   int num;
   NumExp(int n) 
   {
	   num=n;
   }
   
   public int evaluate()
   {
	   return num;
   }
}

class OpExp implements Exp 
{
   Exp left, right; 
   int oper;
   final static int Plus=1,Minus=2,Times=3,Div=4;
   OpExp(Exp l, int o, Exp r) 
   {
	   left=l; 
	   oper=o; 
	   right=r;
   }
   
   public int evaluate()
   {
	   switch(oper)
	   {
	   case Plus:
		   return left.evaluate() + right.evaluate();		   
	   case Minus:
		   return left.evaluate() - right.evaluate();		   
	   case Times:
	   	return left.evaluate() * right.evaluate();	   	
	   case Div:
		   return left.evaluate() / right.evaluate();		   
	   default:
		   throw new RuntimeException("Invalid operator!");			   
	   }
	   
   }
}

class EseqExp implements Exp 
{
   Stm stm; Exp exp;
   EseqExp(Stm s, Exp e) 
   {
	   stm=s; 
	   exp=e;
   }
   
   public int evaluate()
   {
	   //Side effect only
	   stm.evaluate();
	   
	   return exp.evaluate();
   }
}

interface ExpList 
{	
}

class PairExpList implements ExpList 
{
   Exp head; ExpList tail;
   public PairExpList(Exp h, ExpList t) 
   {
	   head=h; 
	   tail=t;
   }  

}

class LastExpList implements ExpList 
{
   Exp head; 
   public LastExpList(Exp h) 
   {
	   head=h;
   }
}
