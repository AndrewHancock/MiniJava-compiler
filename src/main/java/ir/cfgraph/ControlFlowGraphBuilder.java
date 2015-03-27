package ir.cfgraph;

import ir.ops.ConditionalJump;
import ir.ops.Jump;
import ir.ops.Label;
import ir.ops.Statement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ControlFlowGraphBuilder
{
	private Map<CodePoint, CodePoint> statementMap = new HashMap<CodePoint, CodePoint>();

	public Statement buildCfg(List<Statement> statements)
	{
		Statement entryNode = null;
		LinearCodePoint currentNode = null;
		BranchCodePoint currentBranch = null;
		Stack<Integer> branchIndex = new Stack<Integer>();
		for (int i = 0; i < statements.size(); i++)
		{
			Statement statement = statements.get(i);
			if (statement instanceof ConditionalJump)
			{
				branchIndex.push(i);
				currentBranch = new BranchCodePoint((ConditionalJump) statement);
				currentNode = null;
				i = seekLabel(statements, i, currentBranch.getCondition().getLabel());
			}
			else if (statement instanceof Label)
			{
				Label label = (Label) statement;
				if (label == Label.END
						&& (currentBranch.getNotTakenSuccessor() == null || currentBranch.getTakenSuccessor() == null)	)
				{
					i = branchIndex.pop() + 1;
					currentNode = null;
				}
				else
					i++;
			}
			else
			{
				LinearCodePoint newNode = null;
				if (currentNode == null && currentBranch != null)
				{
					newNode = new LinearCodePoint(statement);
					if (currentBranch.getTakenSuccessor() == null)
						currentBranch.setTakenSuccessor(newNode);
					else
						currentBranch.setNotTakenSuccessor(newNode);
				}
				else if (currentNode != null)
					currentNode.setSuccessor(newNode);
				currentNode = newNode;

				if (statement instanceof Jump)
					i = seekLabel(statements, i, ((Jump) statement).getLabel());
			}
		}
		return entryNode;
	}

	private static int seekLabel(List<Statement> statements, int startIndex,
			Label label)
	{
		for (int i = startIndex; i < statements.size(); i = label == Label.TEST ? i - 1 : i + 1)
		{
			Statement statement = statements.get(i);
			if (statement instanceof Label && ((Label) statement) == label)
			{
				return i;
			}
		}
		throw new RuntimeException("Expected Label Not Found.");
	}

}
