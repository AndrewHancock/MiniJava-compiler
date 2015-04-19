package syntaxtree;
import syntaxtree.visitor.TypeVisitor;
import syntaxtree.visitor.Visitor;

public class IdentifierType extends Type {
  public String s;

  public IdentifierType(String as) {
    s=as;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
  
  @Override
  public String toString()  
  {
      return "syntaxtree.IdentifierType";
  }  
}
