package ir;

import java.util.List;

public class SysCall extends Call
{

	public SysCall(String id, List<Alias> parameters, Alias dest)
	{
		super(id, parameters, dest);
	}

}
