package rankserver;

import rankserver.databasecommunication.DataLayer;

import java.rmi.RemoteException;

/**
 * Launcher for the Rank Server.
 * This class makes a new server object and sends a message to the console when it has successfully made one.
 */
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
