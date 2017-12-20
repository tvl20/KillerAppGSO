package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILoginServer extends Remote
{
    /**
     * Try to login with specified username and password.
     * @param username Username for the login.
     * @param password Password for the login.
     * @return The created player object.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    Player logIn(String username, String password) throws RemoteException;

    /**
     * Try to register a new account with the specified username and password.
     * @param username Username for the new account.
     * @param password Password for the new account.
     * @return Whether the register was successful or not.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    boolean register(String username, String password) throws RemoteException;

    /**
     * Get the ranking of all the players.
     * @return The ranking of all the players.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    List<Player> getCurrentRanking() throws RemoteException;
}
