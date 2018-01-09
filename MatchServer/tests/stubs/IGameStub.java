package stubs;

import shared.DTOClientUpdate;
import shared.IGame;
import shared.IMatch;
import shared.Player;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;

public class IGameStub implements IGame
{
    private boolean wonMatch = false;

    private Player localPlayer;
    private IMatch serverMatch;
    private boolean starting;

    public IGameStub(Player localPlayer)
    {
        this.localPlayer = localPlayer;
    }

    @Override
    public void setServerMatch(IMatch serverMatch, boolean starting) throws RemoteException
    {
        this.serverMatch = serverMatch;
        this.starting = starting;
    }

    @Override
    public Player getLocalPlayer() throws RemoteException
    {
        return localPlayer;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        if (evt.getNewValue() instanceof DTOClientUpdate)
        {
            DTOClientUpdate update = (DTOClientUpdate) evt.getNewValue();
            if (update.getVictoriousPlayer() != null)
            {
                wonMatch = update.getVictoriousPlayer().equals(localPlayer);
            }
        }
    }
}
