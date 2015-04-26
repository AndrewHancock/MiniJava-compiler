package ir.cfgraph;

import ir.ops.ConditionalJump;
import ir.ops.Jump;
import ir.ops.Label;
import ir.ops.Statement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlFlowGraphBuilder
{
	private static Map<String, LinearCodePoint> getLabelMap(List<Statement> statements)
	{
		Map<String, LinearCodePoint> labelMap = new HashMap<String, LinearCodePoint>();

		LinearCodePoint currentParent = null;
		for (Statement statement : statements)
		{
			if (statement instanceof Label)
			{
				currentParent = new LinearCodePoint(statement);
				labelMap.put(((Label) statement).getLabel(), currentParent);
			}
		}
		return labelMap;
	}

	public static FlowGraph getCfg(List<Statement> statements)
	{
		Map<String, LinearCodePoint> labelMap = getLabelMap(statements);

		CodePoint entryPoint = null;
		LinearCodePoint previousNode = null;
		BranchCodePoint incompleteBranch = null;

		for (Statement statement : statements)
		{
			if(statement instanceof Label)
			{
				LinearCodePoint label = labelMap.get(((Label) statement).getLabel());
				if(previousNode != null)
				{
					previousNode.setSuccessor(label);
					label.addParent(previousNode);					
				}
				previousNode = label;
			}
			else
			{
				CodePoint newNode = null;
				if (statement instanceof ConditionalJump)
				{
					BranchCodePoint newBranch = new BranchCodePoint(
							(ConditionalJump) statement);
					CodePoint targetLabel = labelMap
							.get(((ConditionalJump) statement).getLabel().getLabel());
					newBranch.setTakenSuccessor(targetLabel);
					targetLabel.addParent(newBranch);
					newNode = newBranch;
					if (incompleteBranch != null)
					{
						newBranch.addParent(incompleteBranch);
						incompleteBranch.setNotTakenSuccessor(newBranch);
					}
					else if(previousNode != null)
					{
						newBranch.addParent(previousNode);
						previousNode.setSuccessor(newBranch);
						previousNode = null;
					}
					incompleteBranch = newBranch;
					
				}
				else
				{
					LinearCodePoint newLinearNode = new LinearCodePoint(statement);

					if (incompleteBranch != null)
					{
						newLinearNode.addParent(incompleteBranch);
						incompleteBranch.setNotTakenSuccessor(newLinearNode);						
						incompleteBranch = null;
					}
					else if (previousNode != null)
					{
						newLinearNode.addParent(previousNode);
						previousNode.setSuccessor(newLinearNode);						
					}
					
					if(statement instanceof Jump)
					{
						CodePoint targetLabel = labelMap
								.get(((Jump) statement).getLabel().getLabel());
						targetLabel.addParent(newLinearNode);
						newLinearNode.setSuccessor(targetLabel);
						previousNode = null;
					}
					else
						previousNode = newLinearNode;
					newNode = newLinearNode;
				}

				if (entryPoint == null)
					entryPoint = newNode;
			}
		}
		return new FlowGraph(entryPoint, previousNode);
	}
}
