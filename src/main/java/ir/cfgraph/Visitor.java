package ir.cfgraph;

public interface Visitor
{
	void visit(BranchCodePoint codePoint);
	void visit(LinearCodePoint codePoint);
}
