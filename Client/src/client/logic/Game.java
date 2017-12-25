package client.logic;

import client.gui.IGUI;
import com.sun.media.jfxmedia.logging.Logger;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import shared.DTOClientUpdate;
import shared.IGame;
import shared.IMatch;
import shared.Player;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Game extends UnicastRemoteObject implements IGame
{
    private final IGUI uiFeedback;

    private final Color clientColor = Color.YELLOW;
    private final Color opponentColor = Color.RED;
    private final int boardWidth = 7;
    private final int boardHeight = 6;
    private Color[][] board = new Color[boardWidth][boardHeight];

    private Player localPlayer;

    private IMatch servermatch = null;

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
            Logger.logMsg(0, "key could not be played, error reaching server: " + e.getMessage());
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

        if (update.getVictoriousPlayer() != null)
        {
            System.out.println("Player won: " + update.getVictoriousPlayer().getUsername());
        }

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

    }

    // TODO: FIX GAME CRASHING AFTER PUTTING THE 5TH KEY IN A COLUMN
    private void addKeyToBoard(Color keyColor, int column, int row)
    {
        board[column][row] = keyColor;
        Platform.runLater(() -> uiFeedback.drawKey(column, row + 1, keyColor));
    }
}
