package client.logic;

import client.gui.IGUI;
import shared.IGameServer;
import shared.ILoginServer;
import shared.Player;

import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the central point of all the logic.
 * It is also the class the GUI talks to if it needs anything from the logic.
 */
public class AppLogic implements ILogic
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    private final IGUI ui;

    private ILoginServer loginServer;
    private IGameServer matchServer;
    private Game game;
    private Player localPlayer = null;

    private static final String RANKING_SERVER_BINDING_NAME = "RankServer";
    private static final String MATCH_SERVER_BINDING_NAME = "MatchServer";

    private static final int RANK_SERVER_BINDING_PORT = 1099;
    private static final String RANK_SERVER_HOST_ADRESS = "localhost";

    private static final int MATCH_SERVER_BINDING_PORT = 1099;
    private static final String MATCH_SERVER_HOST_ADRESS = "localhost";

    public AppLogic(IGUI ui)
    {
        this.ui = ui;

        // Locate rankServerRegistry at IP address and port number
        Registry rankServerRegistry;
        try
        {
            rankServerRegistry = LocateRegistry.getRegistry(RANK_SERVER_HOST_ADRESS, RANK_SERVER_BINDING_PORT);
            DEBUG_LOGGER.log(Level.INFO, "Rank server registry located");
        } catch (RemoteException ex)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error locating rankServer; RemoteException: " + ex.getMessage());
            return;
        }

        // Get the login server from the rankServerRegistry
        try
        {
            loginServer = (ILoginServer) rankServerRegistry.lookup(RANKING_SERVER_BINDING_NAME);
            DEBUG_LOGGER.log(Level.INFO, "LoginServer located");
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "LoginServer couldn't be contacted in the Registry; " + e.getMessage());
        }
        catch (NotBoundException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "LoginServer wasn't bound in the Registry; " + e.getMessage());
        }

        // Locate rankServerRegistry at IP address and port number
        Registry matchServerRegistry;
        try
        {
            matchServerRegistry = LocateRegistry.getRegistry(MATCH_SERVER_HOST_ADRESS, MATCH_SERVER_BINDING_PORT);
            DEBUG_LOGGER.log(Level.INFO, "Rank server registry located");
        } catch (RemoteException ex)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Cannot locate match server registry; " + ex.getMessage());
            return;
        }

        try
        {
            matchServer = (IGameServer) matchServerRegistry.lookup(MATCH_SERVER_BINDING_NAME);
            DEBUG_LOGGER.log(Level.INFO, "matchServer located");
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error contacting match registry; " + e.getMessage());
        }
        catch (NotBoundException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "MatchServer wasn't bound in the Registry; " + e.getMessage());
        }
    }

    @Override
    public boolean addMove(int column)
    {
        return game.playKey(column);
    }

    @Override
    public boolean logIn(String username, String password)
    {
        try
        {
            localPlayer = loginServer.logIn(username, generateMD5Hash(password));
            game = new Game(ui, localPlayer);
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error contacting login server; " + e.getMessage());
        }

        return localPlayer != null;
    }

    @Override
    public boolean register(String username, String password)
    {
        boolean success = false;
        try
        {
            success = loginServer.register(username, generateMD5Hash(password));
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error contacting login server; " + e.getMessage());
        }
        return success;
    }

    @Override
    public void joinMatch()
    {
        if (localPlayer == null)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "No local player set yet.");
            return;
        }

        try
        {
            matchServer.joinGameQueue(game);
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error contacting match server; " + e.getMessage());
        }
    }

    @Override
    public void resetLocalGame()
    {
        try
        {
            game = new Game(ui, localPlayer);
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error creating local game object; " + e.getMessage());
        }
    }

    @Override
    public List<Player> getCurrentRanking()
    {
        List<Player> ranking = new ArrayList<>();
        try
        {
            ranking = loginServer.getCurrentRanking();
        }
        catch (RemoteException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error contacting ranking server; " + e.getMessage());
        }
        return ranking;
    }

    /**
     * Generate an MD5 hash to store in the database. (Used for passwords).
     * (Source: https://stackoverflow.com/a/421696)
     * @param original The original String that should be turned into MD5.
     * @return The hashed MD5 String.
     */
    private String generateMD5Hash(String original)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(original.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            StringBuilder hashText = new StringBuilder(bigInt.toString(16));

            // Add 0's to get the complete 32 characters
            while(hashText.length() < 32 ){
                hashText.insert(0, "0");
            }

            return hashText.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "Error generating MD5 hash. Algorithm not found.");
            return "";
        }
    }
}
