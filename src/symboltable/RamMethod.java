package symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import syntaxtree.Type;

public class RamMethod
{
    private String id;
    private Type returnType;
    private HashMap<String, RamVariable> locals;
    
    //This list holds references to ids which are also in the locals map.
    // This allows access them by index.
    private List<String> params;
    
    public RamMethod(String id, Type type)
    {
        this.id = id;
        returnType = type;
        locals = new HashMap<String,RamVariable>();
        params = new ArrayList<String>();        
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
    
    public RamVariable getParam(String id)
    {
        // Make sure the id is actually a param and not a local
        if(params.contains(id))
            return locals.get(id); 
        else
            return null;
    }
    
    public int getNumberOfParams()
    {
        return params.size();
    }
    
    public boolean addParam(String id, Type type)
    {
        if(params.contains(id))
            return false;        
        params.add(id);
        locals.put(id, new RamVariable(id, type));
        return true;
    }
    
    public RamVariable getParamAt(int i)
    {
        return locals.get(params.get(i));
    }
    
    @Override
    public String toString()
    {
        String result = "\t\t\tclass " + returnType + " " + id + ":\n";
        result += "\t\t\t\tParams:\n";
        for(String id : params)
        {
            result += "\t\t\t\t\t" + locals.get(id) + "\n";
        }
        result += "\t\t\t\tLocals:\n";
        for(RamVariable local : locals.values())
        {
            if(!params.contains(local.id()))
                result += "\t\t\t\t\t" +local + "\n";
        }
        return result;
    }
}
