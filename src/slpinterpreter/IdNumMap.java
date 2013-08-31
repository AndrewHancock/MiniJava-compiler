package slpinterpreter;

public interface IdNumMap
{
    void update(String id, int num);
    int lookup(String id);
}
