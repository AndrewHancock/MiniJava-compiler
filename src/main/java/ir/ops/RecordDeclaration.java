package ir.ops;

import ir.visitor.IrVisitor;

import java.util.ArrayList;
import java.util.List;

public class RecordDeclaration extends Declaration
{
	private List<DataType> fields = new ArrayList<DataType>();	
	
	public RecordDeclaration(String namespace, String id)
	{
		super(namespace, id);
		
	}
	
	public void addField(DataType type)
	{
		fields.add(type);		
	}
	
	public DataType getField(int i)
	{
		return fields.get(i);
	}
	
	public int getFieldCount()
	{
		return fields.size();
	}
	
	@Override
	public void accept(IrVisitor visitor)
	{
		visitor.visit(this);
	}
}
