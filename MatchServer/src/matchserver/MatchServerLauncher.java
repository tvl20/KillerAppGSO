package matchserver;

import java.rmi.RemoteException;

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
