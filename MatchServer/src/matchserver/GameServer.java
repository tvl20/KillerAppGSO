package matchserver;

import shared.IGame;
import shared.IGameServer;
import shared.ILoginServer;
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

public class GameServer extends UnicastRemoteObject implements IGameServer
{
    private List<IGame> clientQueue;
    private List<Match> activeMatches;

    private final String matchServerBindingName = "MatchServer";
    private final String rankingServerBindingName = "RankServer";
    private final int portNumber = 1099;
    private final String hostAdress = "localhost";
    private IRankingServer rankingServer;

    private Timer queueTimer;

    public GameServer() throws RemoteException
    {
        clientQueue = new ArrayList<>();
        activeMatches = new ArrayList<>();

        // Locate registry at IP address and port number
        Registry registry = null;
        try
        {
            registry = LocateRegistry.getRegistry(hostAdress, portNumber);
            System.out.println("Registry located");
        }
        catch (RemoteException ex)
        {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
        }

        // Get the ranking server from the registry
        try
        {
            if (registry != null)
            {
                rankingServer = (IRankingServer) registry.lookup(rankingServerBindingName);
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

        // Bind this using registry
        try
        {
            if (registry != null)
            {
                registry.rebind(matchServerBindingName, this);
                System.out.println("Match Server bound to the registry");
            }
        }
        catch (RemoteException ex)
        {
            System.out.println("Server: Cannot bind match server to registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }

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

                    activeMatches.add(new Match(client, clientOpponent, rankingServer));
                }
                catch (RemoteException e)
                {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    @Override
    public void joinGameQueue(IGame localGame)
    {
        clientQueue.add(localGame);
    }
}
