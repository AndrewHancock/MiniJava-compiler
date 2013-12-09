package symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import syntaxtree.Type;

public class RamMethod
{
    private String id;
    private Type returnType;
    private HashMap<String, RamVariable> locals;    
    private HashMap<String, RamVariable> params;
    //This list holds references to ids which are also in the locals map.
    // This allows access them by index.
    private List<String> paramArrayList;
    
    public RamMethod(String id, Type type)
    {
        this.id = id;
        returnType = type;
        locals = new HashMap<String,RamVariable>();
        params = new HashMap<String, RamVariable>();
        paramArrayList = new ArrayList<String>();        
    }
    
    public String getId()
    {
        return id;
    }
    
    public Type type()
    {
        return returnType;
    }
    
    public RamVariable getVar(String id)
    {
        return locals.get(id);
    }
    
    public boolean addVar(String id, Type type)
    {
        if(locals.get(id) != null)
            return false;
        else
            locals.put(id, new RamVariable(id, type));
        return true;
    }    
    
    public int getNumberOfVars()
    {
        return locals.size();
    }
    
    public Collection<RamVariable> getVarIterable()
    {
        return locals.values();
    }

    public RamVariable getParam(String id)
    {         
        return params.get(id);
    }
    
    public int getNumberOfParams()
    {
        return paramArrayList.size();
    }
    
    public boolean addParam(String id, Type type)
    {
        if(params.containsKey(id))
            return false;        
        paramArrayList.add(id);
        params.put(id, new RamVariable(id, type));
        return true;
    }
    
    public RamVariable getParamAt(int i)
    {
        return params.get(paramArrayList.get(i));
    }
    
    public int getParamIndex(String id)
    {
        for(int i = 0; i < paramArrayList.size(); i++)
            if(paramArrayList.get(i).equals(id))
                return i;
        
        return -1; // Indicates this is not a parameter
    }
    
    @Override
    public String toString()
    {
        String result = "\t\t\tclass " + returnType + " " + id + ":\n";
        result += "\t\t\t\tParams:\n";
        for(String id : paramArrayList)
        {
            result += "\t\t\t\t\t" + locals.get(id) + "\n";
        }
        result += "\t\t\t\tLocals:\n";
        for(RamVariable local : locals.values())
        {
            if(!paramArrayList.contains(local.id()))
                result += "\t\t\t\t\t" +local + "\n";
        }
        return result;
    }
}
