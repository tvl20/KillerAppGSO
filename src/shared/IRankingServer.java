package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRankingServer extends Remote
{
    void rankUp(Player player) throws RemoteException;
    void rankDown(Player player) throws RemoteException;
}
