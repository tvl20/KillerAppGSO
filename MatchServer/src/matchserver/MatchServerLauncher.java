package matchserver;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Launcher for the Match Server.
 * This class makes a new server object and sends a message to the console when it has successfully made one.
 */
public class MatchServerLauncher
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    public static void main(String[] arg)
    {
        try
        {
            GameServer gameServer = new GameServer();
            String logMsg = String.format("done: %s", gameServer.toString());
            DEBUG_LOGGER.log(Level.INFO, logMsg);
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error setting up GameServer object; " + e.getMessage());
        }
    }
}
