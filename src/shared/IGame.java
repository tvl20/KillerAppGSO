package shared;

import shared.fontyspublisher.IRemotePropertyListener;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGame extends Remote, IRemotePropertyListener
{
    void setServerMatch(IMatch serverMatch) throws RemoteException;
    Player getLocalPlayer() throws RemoteException;
}
