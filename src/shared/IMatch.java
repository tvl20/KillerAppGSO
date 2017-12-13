package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMatch extends Remote
{
    boolean playTurn(int playerSessionID, int column) throws RemoteException;
}
