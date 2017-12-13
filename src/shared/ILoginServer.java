package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILoginServer extends Remote
{
    Player logIn(String username, String password) throws RemoteException;
    boolean register(String username, String password) throws RemoteException;
    List<Player> getCurrentRanking() throws RemoteException;
}
