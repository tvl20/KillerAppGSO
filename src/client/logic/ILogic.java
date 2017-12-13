package client.logic;

public interface ILogic
{
    boolean addMove(int column);
    boolean logIn(String username, String password);
    boolean register(String username, String password);
    void joinMatch();
}
