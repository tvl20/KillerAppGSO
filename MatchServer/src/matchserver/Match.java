package matchserver;

import shared.*;
import shared.fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a single match between 2 clients.
 */
public class Match extends UnicastRemoteObject implements IMatch
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    private final transient IGameServerCallback gameServerCallback;

    private static final int BOARD_WIDTH = 7;
    private static final int BOARD_HEIGHT = 6;
    private int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];

    private static final String CLIENT_UPDATE_PROPERTY_NAME = "clientUpdate";

    private final transient IRankingServer rankingServer;
    private final RemotePublisher publisher;
    private final List<Player> activePlayers;

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
        publisher.registerProperty(CLIENT_UPDATE_PROPERTY_NAME);

        publisher.subscribeRemoteListener(gameClient1, CLIENT_UPDATE_PROPERTY_NAME);

        publisher.subscribeRemoteListener(gameClient2, CLIENT_UPDATE_PROPERTY_NAME);

        publisher.inform(CLIENT_UPDATE_PROPERTY_NAME, null, new DTOClientUpdate(columnLastTurn, rowLastTurn, currentTurnPlayer, victoriousPlayer));
    }

    @Override
    public boolean playTurn(int playerSessionID, int column)
    {
        if (column < 0 || column > BOARD_WIDTH
                || board[column][BOARD_HEIGHT - 1] != 0
                || playerSessionID != currentTurnPlayer.getSessionID()
                || gameWon)
        {
            return false;
        }

        boolean success = addKeyToBoard(playerSessionID, column);
        if (!success)
        {
            DEBUG_LOGGER.log(Level.WARNING, "Adding a key to the board failed");
            return false;
        }
        DEBUG_LOGGER.log(Level.INFO, "Adding a key to the board succeeded");

        gameWon = playerWon();
        DEBUG_LOGGER.log(Level.INFO, "Checked for winning player");
        if (gameWon)
        {
            DEBUG_LOGGER.log(Level.INFO, "Player won, round over");
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
            String logMsg = String.format("Last key added: %d, %d", columnLastTurn, rowLastTurn);
            DEBUG_LOGGER.log(Level.INFO, logMsg);
            publisher.inform(CLIENT_UPDATE_PROPERTY_NAME, null, new DTOClientUpdate(columnLastTurn, rowLastTurn, currentTurnPlayer, victoriousPlayer));
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error informing other clients; " + e.getMessage());
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
                DEBUG_LOGGER.log(Level.SEVERE, "Error ranking other clients; " + e.getMessage());
            }


            gameServerCallback.matchFinished(this);
        }

        return true;
    }

    private boolean addKeyToBoard(int sessionID, int column)
    {
        int row = 0;
        while(row < BOARD_HEIGHT)
        {
            if (board[column][row] == 0)
            {
                board[column][row] = sessionID;
                rowLastTurn = row;
                columnLastTurn = column;
                return true;
            }
            row++;
        }

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
        if ((column <= -1 || column >= BOARD_WIDTH)
                || (row <= -1 || row >= BOARD_HEIGHT))
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
        return Arrays.deepEquals(board, match.board) && activePlayers.equals(match.activePlayers);
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
