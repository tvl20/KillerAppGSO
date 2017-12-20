package matchserver;

import shared.Player;
import shared.IGame;
import shared.IMatch;
import shared.IRankingServer;
import shared.fontyspublisher.RemotePublisher;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Match extends UnicastRemoteObject implements IMatch
{
    private final int boardWidth = 7;
    private final int boardHeight = 6;
    private int[][] board = new int[boardWidth][boardHeight];

    private IRankingServer rankingServer;
    private RemotePublisher publisher;

    private List<Player> activePlayers;
    private Player currentTurnPlayer;
    private int columnLastTurn = -1;

    private boolean gameWon = false;

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
        if (column < 0 || column >= boardWidth
                || board[column][boardHeight] != 0
                || playerSessionID != currentTurnPlayer.getSessionID()
                || gameWon)
        {
            return false;
        }

        addKeyToBoard(playerSessionID, column);

        columnLastTurn = column;

        gameWon = playerWon();
        if (gameWon)
        {
            // TODO NOTIFY THE CLIENTS THAT THE CURRENT CLIENT WON
            return false;
        }

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

        return true;
    }

    private void addKeyToBoard(int sessionID, int column)
    {
        // TODO: CHECK IF [0][0] IS THE BOTTOM LEFT OR TOP LEFT
        int row = 0;
        while(row < boardHeight)
        {
            if (board[column][row] == 0)
            {
                board[column][row] = sessionID;
                return;
            }
            row++;
        }
    }

    private boolean playerWon()
    {
        // TODO WRITE LOGIC THAT LOOKS IF PLAYER WON

        // Control the top left and bottom right


        // Control the top right and bottom left


        // Control horizontal


        // Control vertical


        throw new NotImplementedException();
    }

    private int getPosOnBoard(int column, int row)
    {
        if ((column <= -1 || column >= boardWidth)
                || (row <= -1 || row >= boardHeight))
        {
            return -1;
        }
        return board[column][row];
    }
}
