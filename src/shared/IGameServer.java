package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameServer extends Remote
{
    void joinGameQueue(IGame localGame) throws RemoteException;
}
