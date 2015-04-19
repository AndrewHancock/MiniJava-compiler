package syntaxtree;
import syntaxtree.visitor.TypeVisitor;
import syntaxtree.visitor.Visitor;

public class Print extends Statement {
  public ExpList e;

  public Print(ExpList ae) {
    e=ae; 
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
