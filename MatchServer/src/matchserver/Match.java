package matchserver;

import shared.*;
import shared.fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a single match between 2 clients.
 */
public class Match extends UnicastRemoteObject implements IMatch
{
    private final transient IGameServerCallback gameServerCallback;

    private final int boardWidth = 7;
    private final int boardHeight = 6;
    private int[][] board = new int[boardWidth][boardHeight];

    private final transient IRankingServer rankingServer;
    private final RemotePublisher publisher;
    private final List<Player> activePlayers;

    private final String clientUpdatePropertyName = "clientUpdate";

    private Player currentTurnPlayer = null;
    private Player victoriousPlayer = null;
    private int columnLastTurn = -1;
    private int rowLastTurn = -1;

    private boolean gameWon = false;

    public Match(IGame gameClient1, IGame gameClient2, IRankingServer rankingServer, IGameServerCallback gameServerCallback) throws RemoteException
    {
        this.gameServerCallback = gameServerCallback;
        this.rankingServer = rankingServer;

        gameClient1.setServerMatch(this, true);
        gameClient2.setServerMatch(this, false);

        activePlayers = new ArrayList<>();
        activePlayers.add(gameClient1.getLocalPlayer());
        activePlayers.add(gameClient2.getLocalPlayer());

        currentTurnPlayer = gameClient1.getLocalPlayer();

        publisher = new RemotePublisher();
        publisher.registerProperty(clientUpdatePropertyName);

        publisher.subscribeRemoteListener(gameClient1, clientUpdatePropertyName);

        publisher.subscribeRemoteListener(gameClient2, clientUpdatePropertyName);

        publisher.inform(clientUpdatePropertyName, null, new DTOClientUpdate(columnLastTurn, rowLastTurn, currentTurnPlayer, victoriousPlayer));
    }

    @Override
    public boolean playTurn(int playerSessionID, int column)
    {
        if (column < 0 || column > boardWidth
                || board[column][boardHeight - 1] != 0
                || playerSessionID != currentTurnPlayer.getSessionID()
                || gameWon)
        {
            return false;
        }

        boolean success = addKeyToBoard(playerSessionID, column);
        if (!success)
        {
            System.out.println("Adding a key to the board failed");
            return false;
        }
        System.out.println("Adding a key to the board succeeded");

        gameWon = playerWon();
        System.out.println("Checked for winning player");
        if (gameWon)
        {
            System.out.println("Player won, round over");
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
            System.out.println("Last key added: " + columnLastTurn + ", " + rowLastTurn);
            publisher.inform(clientUpdatePropertyName, null, new DTOClientUpdate(columnLastTurn, rowLastTurn, currentTurnPlayer, victoriousPlayer));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }

        if (gameWon)
        {
            try
            {
                rankingServer.rankUp(victoriousPlayer);

                if (victoriousPlayer.equals(activePlayers.get(0)))
                {
                    rankingServer.rankDown(activePlayers.get(1));
                }
                else
                {
                    rankingServer.rankDown(activePlayers.get(0));
                }
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }


            gameServerCallback.matchFinished(this);
        }

        return true;
    }

    private boolean addKeyToBoard(int sessionID, int column)
    {
        int row = 0;
        while(row < boardHeight)
        {
            System.out.print(board[column][row] + ": ");
            System.out.println(column + ", " + row);
            if (board[column][row] == 0)
            {
                board[column][row] = sessionID;
                System.out.println("");
                rowLastTurn = row;
                columnLastTurn = column;
                return true;
            }
            row++;
        }
        System.out.println("");

        return false;
    }

    /**
     * Check whether or not a player has won.
     * @return Boolean if a player has won.
     */
    private boolean playerWon()
    {
        int playerSessionID = currentTurnPlayer.getSessionID();
        int rowLength = -1;
        int counter = 0;

        // Control the top left and bottom right
        while (playerSessionID == getPosOnBoard(columnLastTurn + counter, rowLastTurn + counter))
        {
            rowLength++;
            counter++;
        }
        counter = 0;
        while (playerSessionID == getPosOnBoard(columnLastTurn - counter, rowLastTurn - counter))
        {
            rowLength++;
            counter++;
        }

        if (rowLength >= 4) return true;

        rowLength = -1;
        counter = 0;

        // Control the top right and bottom left
        while (playerSessionID == getPosOnBoard(columnLastTurn - counter, rowLastTurn + counter))
        {
            rowLength++;
            counter++;
        }
        counter = 0;
        while (playerSessionID == getPosOnBoard(columnLastTurn + counter, rowLastTurn - counter))
        {
            rowLength++;
            counter++;
        }

        if (rowLength >= 4) return true;

        rowLength = -1;
        counter = 0;

        // Control horizontal
        while (playerSessionID == getPosOnBoard(columnLastTurn - counter, rowLastTurn))
        {
            rowLength++;
            counter++;
        }
        counter = 0;
        while (playerSessionID == getPosOnBoard(columnLastTurn + counter, rowLastTurn))
        {
            rowLength++;
            counter++;
        }

        if (rowLength >= 4) return true;

        rowLength = -1;
        counter = 0;


        // Control vertical
        while (playerSessionID == getPosOnBoard(columnLastTurn, rowLastTurn - counter))
        {
            rowLength++;
            counter++;
        }
        counter = 0;
        while (playerSessionID == getPosOnBoard(columnLastTurn, rowLastTurn + counter))
        {
            rowLength++;
            counter++;
        }

        return rowLength >= 4;
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

        Match match = (Match) o;

        if (gameWon != match.gameWon)
        {
            return false;
        }
        if (!Arrays.deepEquals(board, match.board))
        {
            return false;
        }
        return activePlayers.equals(match.activePlayers);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + Arrays.deepHashCode(board);
        result = 31 * result + activePlayers.hashCode();
        result = 31 * result + (gameWon ? 1 : 0);
        return result;
    }
}
