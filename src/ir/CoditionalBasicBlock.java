package ir;

public class CoditionalBasicBlock extends BasicBlock
{
	BasicBlock condition = new BasicBlock();
	BasicBlock trueBlock = new BasicBlock();
	BasicBlock falseBlock = new BasicBlock();
}
