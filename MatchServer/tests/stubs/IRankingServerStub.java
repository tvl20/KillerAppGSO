package stubs;

import shared.IRankingServer;
import shared.Player;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class IRankingServerStub implements IRankingServer
{
    private Player rankedUpPlayer;
    private Player rankedDownPlayer;

    public Player getRankedUpPlayer()
    {
        return rankedUpPlayer;
    }

    public Player getRankedDownPlayer()
    {
        return rankedDownPlayer;
    }

    @Override
    public List<Player> getCurrentRanking() throws RemoteException
    {
        return new ArrayList<>();
    }

    @Override
    public void rankUp(Player player) throws RemoteException
    {
        rankedUpPlayer = player;
    }

    @Override
    public void rankDown(Player player) throws RemoteException
    {
        rankedDownPlayer = player;
    }
}
