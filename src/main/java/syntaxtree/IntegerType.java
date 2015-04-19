package syntaxtree;
import syntaxtree.visitor.TypeVisitor;
import syntaxtree.visitor.Visitor;

public class IntegerType extends Type {
  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
  
  @Override
  public String toString()  
  {
      return "syntaxtree.IntegerType";
  }  
}
