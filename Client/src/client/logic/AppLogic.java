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

public class AppLogic implements ILogic
{
    // TODO USE DIFFERENT REGISTRIES FOR DIFFERENT COMPONENTS
    // TODO ONE FOR THE GAME SERVER AND ONE FOR THE RANKING SERVER

    private final IGUI ui;

    private ILoginServer loginServer;
    private IGameServer MatchServer;
    private Game game;
    private Player localplayer = null;

    private final String rankingServerBindingName = "RankServer";
    private final String matchServerBindingName = "MatchServer";

    private final int portNumber = 1099;
    private final String hostAdress = "localhost";
    private Registry registry;

    public AppLogic(IGUI ui)
    {
        this.ui = ui;

        // Locate registry at IP address and port number
        try
        {
            registry = LocateRegistry.getRegistry(hostAdress, portNumber);
            System.out.println("Registry located");
        } catch (RemoteException ex)
        {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            registry = null;
        }

        // Get the login server from the registry
        try
        {
            loginServer = (ILoginServer) registry.lookup(rankingServerBindingName);
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

        try
        {
            MatchServer = (IGameServer) registry.lookup(matchServerBindingName);
            System.out.println("MatchServer located");
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            System.out.println("MatchServer couldn't be contacted in the Registry");
        }
        catch (NotBoundException e)
        {
            e.printStackTrace();
            System.out.println("MatchServer wasn't bound in the Registry");
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
            localplayer = loginServer.logIn(username, generateMD5Hash(password));
            game = new Game(ui, localplayer);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            System.out.println("Unable to login");
        }

        return localplayer != null;
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
        if (localplayer == null)
        {
            System.out.println("No local player set yet");
            return;
        }

        try
        {
            MatchServer.joinGameQueue(game);
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
            game = new Game(ui, localplayer);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

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
