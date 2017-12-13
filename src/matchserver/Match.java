package matchserver;

import shared.Player;
import shared.IGame;
import shared.IMatch;
import shared.IRankingServer;
import shared.fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Match extends UnicastRemoteObject implements IMatch
{
    private IRankingServer rankingServer;
    private RemotePublisher publisher;

    private List<Player> activePlayers;
    private Player currentTurnPlayer;
    private int columnLastTurn = -1;

    public Match(IGame gameClient1, IGame gameClient2, IRankingServer rankingServer) throws RemoteException
    {
        this.rankingServer = rankingServer;
        gameClient1.setServerMatch(this);
        gameClient2.setServerMatch(this);

        activePlayers = new ArrayList<>();
        activePlayers.add(gameClient1.getLocalPlayer());
        activePlayers.add(gameClient2.getLocalPlayer());

        publisher = new RemotePublisher();
        publisher.registerProperty("columnLastTurn");
        publisher.registerProperty("currentTurnPlayer");

        publisher.subscribeRemoteListener(gameClient1, "columnLastTurn");
        publisher.subscribeRemoteListener(gameClient1, "currentTurnPlayer");

        publisher.subscribeRemoteListener(gameClient2, "columnLastTurn");
        publisher.subscribeRemoteListener(gameClient2, "currentTurnPlayer");

        publisher.inform("columnLastTurn", null, columnLastTurn);
        publisher.inform("currentTurnPlayer", null, currentTurnPlayer);
    }

    // TODO: TEST FOR WINNER AND BROADCAST THIS
    @Override
    public boolean playTurn(int playerSessionID, int column)
    {
        columnLastTurn = column;

        if (activePlayers.get(0).equals(currentTurnPlayer))
        {
            currentTurnPlayer = activePlayers.get(1);
        }
        else
        {
            currentTurnPlayer = activePlayers.get(0);
        }

        try
        {
            publisher.inform("columnLastTurn", null, columnLastTurn);
            publisher.inform("currentTurnPlayer", null, currentTurnPlayer);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
