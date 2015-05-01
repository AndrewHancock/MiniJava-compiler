package ir.backend;

import ir.ops.FunctionDeclaration;
import ir.ops.Identifier;
import ir.regalloc.GraphColorAllocator;
import ir.regalloc.RegisterAllocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class WinX64RegisterManager implements RegisterManager
{
	private final int WORD_SIZE = 8;
	private final Register[] callParamRegisters = { new Register("rcx"),
			new Register("rdx"), new Register("r8"), new Register("r9") };
	private final Register[] calleeSaveRegisters = { new Register("r12"),
			new Register("r13"), new Register("r14"), new Register("r15"),
			new Register("rdi"), new Register("rsi") };
	private final Register[] callerSaveRegisters = { new Register("r10"),
			new Register("r11") };
	private final Register reservedRegister = new Register("rax");

	private RegisterAllocator allocator = new GraphColorAllocator();
	private Map<String, Value> idToValueMapping = new HashMap<String, Value>();
	private Map<Value, String> valueToIdMapping = new HashMap<Value, String>();

	private void allocateRegisters(FunctionDeclaration f)
	{
		Map<String, ir.regalloc.Value> callerSavedAllocation = allocator
				.allocateRegisters(f, callerSaveRegisters.length
						+ calleeSaveRegisters.length);
		idToValueMapping.clear();
		for (Entry<String, ir.regalloc.Value> entry : callerSavedAllocation
				.entrySet())
		{
			if (entry.getValue() instanceof ir.regalloc.Register)
				idToValueMapping.put(
						entry.getKey(),
						new Register(
								getRegisterByIndex(((ir.regalloc.Register) entry
										.getValue()).getRegisterIndex())));
			else if (entry.getValue() instanceof ir.regalloc.StackOffset)
				idToValueMapping.put(
						entry.getKey(),
						getStackOffset(((ir.regalloc.StackOffset) entry.getValue())
								.getStackOffset() * -1 - 1));

		}
		assignInputParameters(f.getParams());
		valueToIdMapping.clear();
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
			idToValueMapping.put(params.get(paramCount).getId(),
					callParamRegisters[paramCount]);
		}

		for (; paramCount < params.size() - 1; paramCount++)
		{
			// 48 - Previous frame rbp + return address + "shadow space"
			idToValueMapping.put(params.get(paramCount).getId(),
					getStackOffset(paramCount * WORD_SIZE + 48));
		}
	}

	public String valueString(String id)
	{
		Value value = idToValueMapping.get(id);
		return value.toString();
	}

	public String idString(Value value)
	{
		if (value instanceof IntegerLiteral)
		{
			return value.toString();
		}
		if (value instanceof RegisterDereference)
		{
			return " value at address of " + getReservedRegister().toString();
		}
		else
			return valueToIdMapping.get(value);

	}

	public int numCallParams()
	{
		return callParamRegisters.length;
	}

	public Register getParamReg(int i)
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

	public Register getCalleeSavedReg(int i)
	{
		return calleeSaveRegisters[i];
	}
	
	public int getCalleeSavedCount()
	{
		return calleeSaveRegisters.length;
	}
	
	public int getAssignedCalleeSavedCount()
	{
		int result = allocator.getNumRegistersUsed() - callerSaveRegisters.length;
		if(result < 0)
			return 0;
		else
			return result;
	}

	public int getCallerSavedCount()
	{
		return callerSaveRegisters.length;
	}

	public Register getCallerSavedReg(int i)
	{
		return callerSaveRegisters[i];
	}

	public RegisterAllocator getAllocator()
	{
		return allocator;
	}

	@Override
	public Register getRegisterByIndex(int i)
	{
		if (i < callerSaveRegisters.length)
			return callerSaveRegisters[i];
		else
			return calleeSaveRegisters[i - callerSaveRegisters.length];
	}

	private StackOffset getStackOffset(int baseOffset)
	{
		String result = "" + (WORD_SIZE * baseOffset) + "(%rbp)";
		return new StackOffset(result);
	}

	public Register getReservedRegister()
	{
		return reservedRegister;
	}

	public StackOffset getParamSpill(Value val)
	{
		for(int i = 0; i < callParamRegisters.length; i++)
			if(callParamRegisters[i].toString().equals(val.toString()))
				return new StackOffset((i * WORD_SIZE + 16) + "(%rbp)");
		return null;
	}

	public boolean isAssignedParam(Value value)
	{
		if (!(value instanceof Register))
			return false;
		for (Register param : callParamRegisters)
			if (param.toString().equals(value.toString()))
				return true;
		return false;
	}
}
