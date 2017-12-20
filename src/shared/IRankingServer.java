package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRankingServer extends Remote
{
    /**
     * Get the ranking of all the players.
     * @return The ranking of all the players.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    List<Player> getCurrentRanking() throws RemoteException;

    /**
     * Increase the rank of the specified player.
     * @param player The player who's rank should be increased.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    void rankUp(Player player) throws RemoteException;

    /**
     * Decrease the rank of the specified player.
     * @param player The player who's rank should be decreased.
     * @throws RemoteException This is a remote call, therefore an error can occur.
     */
    void rankDown(Player player) throws RemoteException;
}
