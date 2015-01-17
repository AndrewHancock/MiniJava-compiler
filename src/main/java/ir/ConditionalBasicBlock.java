package ir;

public class ConditionalBasicBlock extends BasicBlock
{
	BasicBlock condition = new BasicBlock();
	BasicBlock trueBlock = new BasicBlock();
	BasicBlock falseBlock = new BasicBlock();
}
