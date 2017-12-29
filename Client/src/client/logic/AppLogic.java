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
import java.util.List;

/**
 * This class is the central point of all the logic.
 * It is also the class the GUI talks to if it needs anything from the logic.
 */
public class AppLogic implements ILogic
{
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
            System.out.println("Rank server registry located");
        } catch (RemoteException ex)
        {
            System.out.println("Client: Cannot locate rankServerRegistry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            rankServerRegistry = null;
            return;
        }

        // Get the login server from the rankServerRegistry
        try
        {
            loginServer = (ILoginServer) rankServerRegistry.lookup(RANKING_SERVER_BINDING_NAME);
            System.out.println("LoginServer located");
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            System.out.println("LoginServer couldn't be contacted in the Registry");
        }
        catch (NotBoundException e)
        {
            e.printStackTrace();
            System.out.println("LoginServer wasn't bound in the Registry");
        }

        // Locate rankServerRegistry at IP address and port number
        Registry matchServerRegistry;
        try
        {
            matchServerRegistry = LocateRegistry.getRegistry(MATCH_SERVER_HOST_ADRESS, MATCH_SERVER_BINDING_PORT);
            System.out.println("Rank server registry located");
        } catch (RemoteException ex)
        {
            System.out.println("Client: Cannot locate rankServerRegistry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            matchServerRegistry = null;
            return;
        }

        try
        {
            matchServer = (IGameServer) matchServerRegistry.lookup(MATCH_SERVER_BINDING_NAME);
            System.out.println("matchServer located");
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            System.out.println("matchServer couldn't be contacted in the Registry");
        }
        catch (NotBoundException e)
        {
            e.printStackTrace();
            System.out.println("matchServer wasn't bound in the Registry");
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
            e.printStackTrace();
            System.out.println("Unable to login");
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
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public void joinMatch()
    {
        if (localPlayer == null)
        {
            System.out.println("No local player set yet");
            return;
        }

        try
        {
            matchServer.joinGameQueue(game);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public List<Player> getCurrentRanking()
    {
        try
        {
            return loginServer.getCurrentRanking();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return null;
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
            e.printStackTrace();
            return "";
        }
    }
}
