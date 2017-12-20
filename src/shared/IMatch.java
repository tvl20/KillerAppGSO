package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMatch extends Remote
{
    /**
     * Add a turn of a player.
     * @param playerSessionID The sessionID of a client.
     * @param column The column of where there a piece should be added.
     * @return Whether the turn was successful or not.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    boolean playTurn(int playerSessionID, int column) throws RemoteException;
}
