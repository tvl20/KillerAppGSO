package rankserver;

import rankserver.databasecommunication.DataLayer;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Launcher for the Rank Server.
 * This class makes a new server object and sends a message to the console when it has successfully made one.
 */
public class RankServerLauncher
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    public static void main(String[] args)
    {
        try
        {
            RankServer rankServer = new RankServer(new DataLayer());
            String logMsg = String.format("done: %s", rankServer.toString());
            DEBUG_LOGGER.log(Level.INFO, logMsg);
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error creating Rank Server object; " + e.getMessage());
        }
    }
}
