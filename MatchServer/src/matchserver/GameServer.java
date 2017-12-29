package matchserver;

import shared.IGame;
import shared.IGameServer;
import shared.IRankingServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class of the Match Server.
 * This class is added to the registry and is contacted everytime a client wants to join a match.
 */
public class GameServer extends UnicastRemoteObject implements IGameServer, IGameServerCallback
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    private transient List<IGame> clientQueue;
    private transient List<Match> activeMatches;

    private static final String MATCH_SERVER_BINDING_NAME = "MatchServer";
    private static final String RANKING_SERVER_BINDING_NAME = "RankServer";
    private static final int RANK_SERVER_REGISTRY_PORT = 1099;
    private static final int MATCH_SERVER_REGISTRY_PORT = 1100;
    private static final String RANK_SERVER_HOST_ADRESS = "localhost";
    private transient IRankingServer rankingServer;

    private transient Timer queueTimer;

    public GameServer() throws RemoteException
    {
        clientQueue = new ArrayList<>();
        activeMatches = new ArrayList<>();

        // Locate rankingServerRegistry at IP address and port number
        Registry rankingServerRegistry = null;
        try
        {
            rankingServerRegistry = LocateRegistry.getRegistry(RANK_SERVER_HOST_ADRESS, RANK_SERVER_REGISTRY_PORT);
            DEBUG_LOGGER.log(Level.INFO, "Registry created");
        }
        catch (RemoteException ex)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Unable to contact ranking server registry; " + ex.getMessage());
        }

        // Get the ranking server from the rankingServerRegistry
        try
        {
            if (rankingServerRegistry != null)
            {
                rankingServer = (IRankingServer) rankingServerRegistry.lookup(RANKING_SERVER_BINDING_NAME);
                DEBUG_LOGGER.log(Level.INFO, "Rank server in registry located.");
            }
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "LoginServer couldn't be contacted in the Registry; " + e.getMessage());
        }
        catch (NotBoundException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "LoginServer wasn't bound in the Registry");
        }

        // Locate and bind this to its own registry
        try
        {
            Registry localRegistry = LocateRegistry.createRegistry(MATCH_SERVER_REGISTRY_PORT);

            localRegistry.rebind(MATCH_SERVER_BINDING_NAME, this);
            DEBUG_LOGGER.log(Level.INFO, "Match Server bound to the matchServerRegistry");
        }
        catch (RemoteException ex)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Could not bind this to the registry; " + ex.getMessage());
        }

        setupMatchMakingTimer();
    }

    @Override
    public void joinGameQueue(IGame localGame)
    {
        clientQueue.add(localGame);
    }

    @Override
    public void matchFinished(Match match)
    {
        activeMatches.remove(match);
    }

    private void setupMatchMakingTimer()
    {
        IGameServerCallback callback = this;
        queueTimer = new Timer();
        queueTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    if (clientQueue.size() < 2)
                    {
                        return;
                    }

                    // Get 2 clients from the array
                    IGame client = clientQueue.get(0);
                    int clientRank = client.getLocalPlayer().getRanking();

                    IGame clientOpponent = clientQueue.get(1);
                    int clientOpponentRank = clientOpponent.getLocalPlayer().getRanking();

                    if (clientQueue.size() > 2)
                    {
                        // Calculate the difference in rank between the selected clients
                        int rankDifference = getRankDifference(clientRank, clientOpponentRank);

                        // Go through the other clients in the queue
                        for (int i = 2; i < clientQueue.size(); i++)
                        {
                            IGame possibleOpponent = clientQueue.get(i);
                            int possibleOpponentRank = possibleOpponent.getLocalPlayer().getRanking();

                            int possibleRankDifference = getRankDifference(clientRank, possibleOpponentRank);

                            // If the rank difference between another client in the queue and the originally selected
                            // client is lower select this client as the opponent instead
                            if (possibleRankDifference < rankDifference)
                            {
                                clientOpponent = possibleOpponent;
                                rankDifference = possibleRankDifference;
                            }
                        }
                    }

                    activeMatches.add(new Match(client, clientOpponent, rankingServer, callback));
                    clientQueue.remove(client);
                    clientQueue.remove(clientOpponent);
                }
                catch (RemoteException e)
                {
                    DEBUG_LOGGER.log(Level.SEVERE, "Error contacting player for rank; " + e.getMessage());
                }
            }
        }, 0, 3000);
    }

    /**
     * Return a positive number of the amount of ranks between the two.
     * @param playerRank1 Rank 1 for the comparison.
     * @param playerRank2 Rank 2 for the comparison.
     * @return A positive number with the rank difference.
     */
    private int getRankDifference(int playerRank1, int playerRank2)
    {
        int rankDifference = playerRank1 - playerRank2;
        if (rankDifference < 0)
        {
            rankDifference = rankDifference * -1;
        }
        return rankDifference;
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
        result = 31 * result + RANK_SERVER_REGISTRY_PORT;
        result = 31 * result + RANK_SERVER_HOST_ADRESS.hashCode();
        return result;
    }
}
