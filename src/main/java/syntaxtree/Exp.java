package syntaxtree;
import syntaxtree.visitor.TypeVisitor;
import syntaxtree.visitor.Visitor;

public abstract class Exp {
  public abstract void accept(Visitor v);
  public abstract Type accept(TypeVisitor v);
}
