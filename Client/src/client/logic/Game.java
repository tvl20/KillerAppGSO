package client.logic;

import client.gui.IGUI;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import shared.DTOClientUpdate;
import shared.IGame;
import shared.IMatch;
import shared.Player;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends UnicastRemoteObject implements IGame
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    private final transient IGUI uiFeedback;

    private static final Color CLIENT_COLOR = Color.YELLOW;
    private static final Color OPPONENT_COLOR = Color.RED;
    private static final int BOARD_WIDTH = 7;
    private static final int BOARD_HEIGHT = 6;
    private Color[][] board = new Color[BOARD_WIDTH][BOARD_HEIGHT];

    private Player localPlayer;

    private transient IMatch servermatch = null;

    public Game(IGUI uiFeedback, Player localPlayer) throws RemoteException
    {
        super();
        this.uiFeedback = uiFeedback;
        this.localPlayer = localPlayer;
    }

    public boolean playKey(int column)
    {
        boolean success;
        try
        {
            success = servermatch.playTurn(localPlayer.getSessionID(), column);
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error playing turn; " + e.getMessage());
            success = false;
        }
        return success;
    }

    @Override
    public void setServerMatch(IMatch serverMatch, boolean starting)
    {
        this.servermatch = serverMatch;
        Platform.runLater(() -> uiFeedback.startGame(starting));
    }

    @Override
    public Player getLocalPlayer()
    {
        return localPlayer;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        DTOClientUpdate update = (DTOClientUpdate) evt.getNewValue();

        final int column = update.getColumnLastTurn();
        final int row = update.getRowLastTurn();
        if (column == -1 || row == -1)
        {
            return;
        }

        if (update.getCurrentTurnPlayer().equals(this.localPlayer))
        {
            addKeyToBoard(OPPONENT_COLOR, column, row);
        }
        else
        {
            addKeyToBoard(CLIENT_COLOR, column, row);
        }

        Player victoriousPlayer = update.getVictoriousPlayer();
        if (victoriousPlayer != null)
        {
            String logMsg = String.format("Player won: %s", update.getVictoriousPlayer().getUsername());
            DEBUG_LOGGER.log(Level.INFO, logMsg);

            if (localPlayer.equals(victoriousPlayer))
            {
                Platform.runLater(() -> uiFeedback.won());
            }
            else
            {
                Platform.runLater(() -> uiFeedback.lost());
            }
        }
    }

    private void addKeyToBoard(Color keyColor, int column, int row)
    {
        board[column][row] = keyColor;
        Platform.runLater(() -> uiFeedback.drawKey(column, row + 1, keyColor));
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

        Game game = (Game) o;

        return Arrays.deepEquals(board, game.board) && getLocalPlayer().equals(game.getLocalPlayer());
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + Arrays.deepHashCode(board);
        result = 31 * result + getLocalPlayer().hashCode();
        return result;
    }
}
