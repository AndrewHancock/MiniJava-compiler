package ir.cfgraph;

public class ConditionalBasicBlock extends BasicBlock
{
	BasicBlock condition;
	BasicBlock trueBlock;
	BasicBlock falseBlock;
	
	public ConditionalBasicBlock(BasicBlock condition, BasicBlock trueBlock, BasicBlock falseBlock)
	{
		this.condition = condition;
		this.trueBlock = trueBlock;
		this.falseBlock = falseBlock;		
	}
	
	public BasicBlock getCondition()
	{
		return condition;
	}
	
	public BasicBlock getTrueBlock()
	{
		return trueBlock;
	}
	
	public BasicBlock getFalseBlock()
	{
		return falseBlock;
	}
}
