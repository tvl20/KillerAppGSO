package rankserver;

import rankserver.databasecommunication.DataLayer;

import java.rmi.RemoteException;

public class RankServerLauncher
{
    public static void main(String[] args)
    {
        try
        {
            RankServer rankServer = new RankServer(new DataLayer());
            System.out.println("done: " + rankServer);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
