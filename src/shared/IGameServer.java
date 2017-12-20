package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameServer extends Remote
{
    /**
     * Join the match making queue with the local game object.
     * @param localGame The local game object of the client who want to join the queue.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    void joinGameQueue(IGame localGame) throws RemoteException;
}
