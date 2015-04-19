package syntaxtree;
import syntaxtree.visitor.TypeVisitor;
import syntaxtree.visitor.Visitor;

public abstract class Statement {
  public abstract void accept(Visitor v);
  public abstract Type accept(TypeVisitor v);
}
