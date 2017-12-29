package matchserver;

import java.rmi.RemoteException;

/**
 * Launcher for the Match Server.
 * This class makes a new server object and sends a message to the console when it has successfully made one.
 */
public class MatchServerLauncher
{
    public static void main(String[] arg)
    {
        try
        {
            GameServer gameServer = new GameServer();
            System.out.println("done: " + gameServer);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
