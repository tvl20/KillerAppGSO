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

public class Game extends UnicastRemoteObject implements IGame
{
    private final transient IGUI uiFeedback;

    private final Color clientColor = Color.YELLOW;
    private final Color opponentColor = Color.RED;
    private final int boardWidth = 7;
    private final int boardHeight = 6;
    private Color[][] board = new Color[boardWidth][boardHeight];

    private Player localPlayer;

    private transient IMatch servermatch = null;

    public Game(IGUI uiFeedback, Player localPlayer) throws RemoteException
    {
        this.uiFeedback = uiFeedback;
        this.localPlayer = localPlayer;
    }

    public boolean playKey(int column)
    {
        try
        {
            return servermatch.playTurn(localPlayer.getSessionID(), column);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            return false;
        }
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
            addKeyToBoard(opponentColor, column, row);
        }
        else
        {
            addKeyToBoard(clientColor, column, row);
        }

        Player victoriousPlayer = update.getVictoriousPlayer();
        if (victoriousPlayer != null)
        {
            System.out.println("Player won: " + update.getVictoriousPlayer().getUsername());

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

        if (!Arrays.deepEquals(board, game.board))
        {
            return false;
        }
        return getLocalPlayer().equals(game.getLocalPlayer());
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
