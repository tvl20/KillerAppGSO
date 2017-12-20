package shared;

import shared.fontyspublisher.IRemotePropertyListener;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGame extends Remote, IRemotePropertyListener
{
    /**
     * Set the server match that will be linked to the local game.
     * @param serverMatch The server match that will be linked.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    void setServerMatch(IMatch serverMatch) throws RemoteException;

    /**
     * Get the local player object of the game.
     * @return The local player object of that game.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    Player getLocalPlayer() throws RemoteException;
}
