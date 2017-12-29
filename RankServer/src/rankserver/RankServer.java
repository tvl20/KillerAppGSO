package rankserver;

import shared.Player;
import rankserver.databasecommunication.IDatabase;
import shared.ILoginServer;
import shared.IRankingServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Main class of the rank / login server.
 * This class will be added to a registry,
 * this class can be used as a login server (ILoginServer) or a rank server (IRankingServer).
 */
public class RankServer extends UnicastRemoteObject implements ILoginServer, IRankingServer
{
    private transient IDatabase database;

    private final String bindingName = "RankServer";
    private final int portNumber = 1099;
    private transient Registry registry;

    public RankServer(IDatabase database) throws RemoteException
    {
        this.database = database;

        // Create registry at port number
        try {
            registry = LocateRegistry.createRegistry(portNumber);
            System.out.println("Server: Registry created on port number " + portNumber);
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot create registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            registry = null;
        }

        // Bind this using registry
        try {
            if (registry != null)
            {
                registry.rebind(bindingName, this);
                System.out.println("Rank Server bound to the registry");
            }
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot bind ranking server to registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }
    }

    @Override
    public Player logIn(String username, String password)
    {
        int playerSessionID = database.login(username, password);
        if (playerSessionID == -1)
        {
            return null;
        }

        int playerRank = database.getPlayerRank(username);
        return new Player(username, playerRank, playerSessionID);
    }

    @Override
    public boolean register(String username, String password)
    {
        return database.register(username, password);
    }

    @Override
    public List<Player> getCurrentRanking()
    {
        return database.getCurrentRanking();
    }

    @Override
    public void rankUp(Player player)
    {
        database.rankUpPlayer(player);
    }

    @Override
    public void rankDown(Player player)
    {
        database.rankDownPlayer(player);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        RankServer that = (RankServer) o;

        if (portNumber != that.portNumber)
        {
            return false;
        }
        return bindingName.equals(that.bindingName);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + bindingName.hashCode();
        result = 31 * result + portNumber;
        return result;
    }
}
