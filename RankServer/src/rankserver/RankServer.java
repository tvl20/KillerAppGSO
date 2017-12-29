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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class of the rank / login server.
 * This class will be added to a registry,
 * this class can be used as a login server (ILoginServer) or a rank server (IRankingServer).
 */
public class RankServer extends UnicastRemoteObject implements ILoginServer, IRankingServer
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    private transient IDatabase database;

    private static final String BINDING_NAME = "RankServer";
    private static final int PORT_NUMBER = 1099;
    private transient Registry registry;

    public RankServer(IDatabase database) throws RemoteException
    {
        this.database = database;

        // Create registry at port number
        try {
            registry = LocateRegistry.createRegistry(PORT_NUMBER);
            String logMsg = String.format("Registry created on port number %d", PORT_NUMBER);
            DEBUG_LOGGER.log(Level.INFO, logMsg);
        } catch (RemoteException ex) {
            DEBUG_LOGGER.log(Level.SEVERE, "Cannot create registry; " + ex.getMessage());
            registry = null;
        }

        // Bind this using registry
        try {
            if (registry != null)
            {
                registry.rebind(BINDING_NAME, this);
                DEBUG_LOGGER.log(Level.INFO, "Rank Server bound to the registry");
            }
        } catch (RemoteException ex) {
            DEBUG_LOGGER.log(Level.SEVERE, "Cannot bind ranking server to registry; " + ex.getMessage());
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
        return this == o || o != null && getClass() == o.getClass() && super.equals(o);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + BINDING_NAME.hashCode();
        result = 31 * result + PORT_NUMBER;
        return result;
    }
}
