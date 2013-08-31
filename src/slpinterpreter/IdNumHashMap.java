package slpinterpreter;

import java.util.HashMap;

public class IdNumHashMap implements IdNumMap
{
    HashMap<String, Integer> idMap = new HashMap<String, Integer>();

    @Override
    public void update(String id, int num)
    {
        idMap.put(id, num);
    }

    @Override
    public int lookup(String id)
    {
        return idMap.get(id);     
    }

}
