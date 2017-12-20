package client.logic;

import com.sun.media.jfxmedia.logging.Logger;
import javafx.scene.paint.Color;
import shared.IGame;
import shared.IMatch;
import shared.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Game extends UnicastRemoteObject implements IGame
{
    private final int boardWidth = 7;
    private final int boardHeight = 6;
    private Color[][] Board = new Color[boardWidth][boardHeight];

    private Player localPlayer;

    private IMatch servermatch = null;

    public Game(Player localPlayer) throws RemoteException
    {
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
    public void setServerMatch(IMatch serverMatch)
    {
        this.servermatch = serverMatch;
    }

    @Override
    public Player getLocalPlayer()
    {
        return localPlayer;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        throw new NotImplementedException();
    }
}
