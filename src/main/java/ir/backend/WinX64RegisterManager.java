package ir.backend;

import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;
import ir.regalloc.GraphColorAllocator;
import ir.regalloc.Register;
import ir.regalloc.RegisterAllocator;
import ir.regalloc.StackOffset;
import ir.regalloc.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class WinX64RegisterManager
{
	private final int WORD_SIZE = 8;
	private final String[] callParamRegisters = { "rcx", "rdx", "r8", "r9" };
	private final String[] calleeSaveRegisters = { "r12", "r13",
			"r14", "r15", "rdi", "rsi" };
	private final String[] callerSaveRegisters = { "r10", "r11" };
	private final String[] reservedRegisters = { "rax", "rbx" };
	
	private RegisterAllocator allocator = new GraphColorAllocator();
	private Map<String, Value> idToValueMapping = new HashMap<String, Value>();
	private Map<Value, String> valueToIdMapping = new HashMap<Value, String>();
	
	
	private void allocateRegisters(FunctionDeclaration f)
	{
		Map<String, Value> callerSavedAllocation = allocator.allocateRegisters(f,
				callerSaveRegisters.length);
		if (allocator.getSpillCount() == 0)
		{
			idToValueMapping = callerSavedAllocation;
		}
		else
		{
			idToValueMapping = allocator.allocateRegisters(f,
					calleeSaveRegisters.length + callerSaveRegisters.length);
		}
		assignInputParameters(f.getParams());
		for (Entry<String, Value> entry : idToValueMapping.entrySet())
		{
			valueToIdMapping.put(entry.getValue(), entry.getKey());
		}
	}

	private void assignInputParameters(List<Identifier> params)
	{
		int paramCount = 0;
		for (paramCount = 0; paramCount < params.size() 
				&& paramCount < callParamRegisters.length; paramCount++)
		{
			idToValueMapping.put(params.get(paramCount).getId(), new Register(
					callerSaveRegisters.length + calleeSaveRegisters.length + paramCount));
		}

		for (; paramCount < params.size() - 1; paramCount++)
		{
			idToValueMapping.put(params.get(paramCount).getId(), new StackOffset(
					paramCount * WORD_SIZE + 16));
		}
	}

	public String valueString(String id)
	{
		Value value = idToValueMapping.get(id);
		return valueString(value);
	}

	public String valueString(Value value)
	{
		if (value instanceof StackOffset)
			return "-" + ((StackOffset) value).getStackOffset() + "(%rsp)";
		else if (value instanceof RegisterDereference)
		{
			return "(%"
					+ reservedRegisters[((RegisterDereference) value)
							.getRegisterIndex()] + ")";
		}
		else if (value instanceof IntegerLiteral)
		{
			return "$" + ((IntegerLiteral) value).getValue();
		}
		else
		{
			int registerOffset = ((Register) value).getRegisterIndex();
			if (registerOffset < callerSaveRegisters.length)
				return "%" + callerSaveRegisters[registerOffset];
			else if (registerOffset < callerSaveRegisters.length + calleeSaveRegisters.length)
				return "%" + calleeSaveRegisters[registerOffset - callerSaveRegisters.length];
			else
				return "%" + callParamRegisters[registerOffset - callerSaveRegisters.length - calleeSaveRegisters.length];
		}
	}

	public String idString(Value value)
	{
		if (value instanceof IntegerLiteral)
		{
			return Integer.toString(((IntegerLiteral) value).getValue());
		}
		if (value instanceof RegisterDereference)
		{
			return " value at address of "
					+ reservedRegisters[((RegisterDereference) value)
							.getRegisterIndex()];
		}
		else
			return valueToIdMapping.get(value);

	}
	
	public int numCallParams()
	{
		return callParamRegisters.length;
	}
	
	public String getParamReg(int i)
	{
		return callParamRegisters[i];
	}
	
	public int wordSize()
	{
		return WORD_SIZE;
	}
	
	public void init(FunctionDeclaration f)
	{
		allocateRegisters(f);
	}
	
	public Value value(String id)
	{
		return idToValueMapping.get(id);
	}
	
	public String getCalleeSavedReg(int i)
	{
		return calleeSaveRegisters[i];
	}
	
	public int getCalleeSavedCount()
	{
		return calleeSaveRegisters.length;
	}
	
	public int getCallerSavedCount()
	{
		return callerSaveRegisters.length;
	}
	
	public String getCallerSavedReg(int i)
	{
		return callerSaveRegisters[i];
	}
	
	public RegisterAllocator getAllocator()
	{
		return allocator;
	}
}
