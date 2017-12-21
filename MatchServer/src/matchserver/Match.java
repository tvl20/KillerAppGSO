package matchserver;

import shared.*;
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
    private Player victoriousPlayer = null;
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

        publisher.subscribeRemoteListener(gameClient1, "clientUpdate");

        publisher.subscribeRemoteListener(gameClient2, "clientUpdate");

        publisher.inform("clientUpdate", null, new DTOClientUpdate(columnLastTurn, currentTurnPlayer, victoriousPlayer));
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
            victoriousPlayer = currentTurnPlayer;
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
            publisher.inform("clientUpdate", null, new DTOClientUpdate(columnLastTurn, currentTurnPlayer, victoriousPlayer));
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
        int rowLength = 1;

        int rowLastTrun = 0;
        for (int i = 0; i < boardHeight; i++)
        {
            if (board[columnLastTurn][i] == 0)
            {
                rowLastTrun = i-1;
            }
        }

        int counter = 1;

        // Control the top left and bottom right
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn + counter, rowLastTrun + counter))
        {
            rowLength++;
        }
        counter = 0;
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn - counter, rowLastTrun - counter))
        {
            rowLength++;
        }

        if (rowLength >= 4) return true;

        counter = 0;
        rowLength = 0;

        // Control the top right and bottom left
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn - counter, rowLastTrun + counter))
        {
            rowLength++;
        }
        counter = 0;
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn + counter, rowLastTrun - counter))
        {
            rowLength++;
        }

        if (rowLength >= 4) return true;

        counter = 0;
        rowLength = 0;

        // Control horizontal
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn - counter, rowLastTrun))
        {
            rowLength++;
        }
        counter = 0;
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn + counter, rowLastTrun))
        {
            rowLength++;
        }

        if (rowLength >= 4) return true;

        counter = 0;
        rowLength = 0;


        // Control vertical
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn, rowLastTrun - counter))
        {
            rowLength++;
        }
        counter = 0;
        while (currentTurnPlayer.getSessionID() == getPosOnBoard(columnLastTurn, rowLastTrun + counter))
        {
            rowLength++;
        }

        if (rowLength >= 4) return true;
        return false;
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
