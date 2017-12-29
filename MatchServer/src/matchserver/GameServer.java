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

/**
 * Main class of the Match Server.
 * This class is added to the registry and is contacted everytime a client wants to join a match.
 */
public class GameServer extends UnicastRemoteObject implements IGameServer, IGameServerCallback
{
    private transient List<IGame> clientQueue;
    private transient List<Match> activeMatches;

    private static final String matchServerBindingName = "MatchServer";
    private static final String rankingServerBindingName = "RankServer";
    private static final int rankServerRegistryPort = 1099;
    private static final int matchServerRegistryPort = 1100;
    private static final String rankServerHostAdress = "localhost";
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
            rankingServerRegistry = LocateRegistry.getRegistry(rankServerHostAdress, rankServerRegistryPort);
            System.out.println("Registry located");
        }
        catch (RemoteException ex)
        {
            System.out.println("Client: Cannot locate rankingServerRegistry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
        }

        // Get the ranking server from the rankingServerRegistry
        try
        {
            if (rankingServerRegistry != null)
            {
                rankingServer = (IRankingServer) rankingServerRegistry.lookup(rankingServerBindingName);
                System.out.println("MatchServer located");
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            System.out.println("LoginServer couldn't be contacted in the Registry");
        }
        catch (NotBoundException e)
        {
            e.printStackTrace();
            System.out.println("LoginServer wasn't bound in the Registry");
        }

        // Bind this using rankingServerRegistry
        try
        {
            Registry localRegistry = LocateRegistry.createRegistry(matchServerRegistryPort);

            localRegistry.rebind(matchServerBindingName, this);
            System.out.println("Match Server bound to the matchServerRegistry");
        }
        catch (RemoteException ex)
        {
            System.out.println("Server: Cannot bind match server to rankingServerRegistry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
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
                    System.out.println("Timer ticked");
                    if (clientQueue.size() < 2)
                    {
                        return;
                    }

                    // Get 2 clients from the array
                    IGame client = clientQueue.get(0);
                    int clientRank = client.getLocalPlayer().getRanking();

                    IGame clientOpponent = clientQueue.get(1);
                    int clientOpponentRank = clientOpponent.getLocalPlayer().getRanking();

                    // Calculate the difference in rank between the selected clients
                    int rankDifference = clientRank - clientOpponentRank;
                    if (rankDifference < 0)
                    {
                        rankDifference = rankDifference * -1;
                    }

                    if (clientQueue.size() > 2)
                    {
                        // Go through the other clients in the queue
                        for (int i = 2; i < clientQueue.size(); i++)
                        {
                            IGame possibleOpponent = clientQueue.get(i);
                            int possibleOpponentRank = possibleOpponent.getLocalPlayer().getRanking();

                            int possibleRankDifference = clientRank - possibleOpponentRank;
                            if (possibleRankDifference < 0)
                            {
                                possibleRankDifference = possibleRankDifference * -1;
                            }

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
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
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

        GameServer that = (GameServer) o;

        if (rankServerRegistryPort != that.rankServerRegistryPort)
        {
            return false;
        }
        return rankServerHostAdress.equals(that.rankServerHostAdress);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + rankServerRegistryPort;
        result = 31 * result + rankServerHostAdress.hashCode();
        return result;
    }
}
