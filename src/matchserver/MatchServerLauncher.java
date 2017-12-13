package matchserver;

import java.rmi.RemoteException;

public class MatchServerLauncher
{
    public static void main(String[] arg)
    {
        try
        {
            GameServer gameServer = new GameServer();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
