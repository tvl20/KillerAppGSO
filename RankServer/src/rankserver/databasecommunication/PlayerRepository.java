package rankserver.databasecommunication;

import shared.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles all the database communication related to Players.
 */
public class PlayerRepository
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    private String connectionString;

    private static final String LOGIN_CHECK_QUERY = "SELECT PlayerName FROM Players WHERE PlayerName = ? AND PlayerPassword = ?";
    private static final String GET_RANK_QUERY = "SELECT PlayerRank FROM Players WHERE PlayerName = ?";
    private static final String CHANGE_RANK_QUERY = "UPDATE Players SET PlayerRank = ? WHERE PlayerName = ?";
    private static final String GET_ALL_RANKS_QUERY = "SELECT PlayerName, PlayerRank FROM Players";
    private static final String REGISTER_USER_QUERY = "INSERT INTO Players (PlayerName, PlayerPassword) VALUES (?,?)";

    public PlayerRepository()
    {
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connectionString = "jdbc:sqlserver://localhost;" +
                    "databaseName=GSOKillerApp;" +
                    "integratedSecurity=true";
        }
        catch (ClassNotFoundException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Check the credentials of a potential login.
     * @param username Login username.
     * @param password Login password.
     * @return the new sessionID of the player, if the login was unsuccessful the returned value will be -1.
     */
    public int logIn(String username, String password)
    {
        boolean successfulLogin = false;
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement loginStatement = connection.prepareStatement(LOGIN_CHECK_QUERY))
        {
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);

            try (ResultSet results = loginStatement.executeQuery())
            {
                while(results.next())
                {
                    successfulLogin = username.equals(results.getString(1));
                }
            }
        }
        catch (SQLException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "SQL error in loggin in; " + e.getMessage());
        }

        if (!successfulLogin)
        {
            return -1;
        }
        else
        {
            Random rnd = new Random();
            return rnd.nextInt(1000000) + 1; // To ensure the SessionID is never 0
        }
    }

    /**
     * Register a new account.
     * @param username Username for the new account.
     * @param password Password for the new account.
     * @return Whether or not the creation of the new account has succeeded.
     */
    public boolean register(String username, String password)
    {
        // Minimum value of integer means that a player with that name does not yet exist
        int rank = getPlayerRank(username);
        if (username.length() >= 256 || rank != Integer.MIN_VALUE)
        {
            String logMsg = String.format("Last key added: %d", rank);
            DEBUG_LOGGER.log(Level.INFO, logMsg);
            return false;
        }

        DEBUG_LOGGER.log(Level.INFO, "Creating new account");

        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement rankUpdateStatement = connection.prepareStatement(REGISTER_USER_QUERY))
        {
            rankUpdateStatement.setString(1, username);
            rankUpdateStatement.setString(2, password);

            rankUpdateStatement.execute();
        }
        catch (SQLException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "SQL error in registering new user; " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Get a list of player objects (with a username and ranking) that represents the current ranking.
     * @return The list of player objects.
     */
    public List<Player> getCurrentRanking()
    {
        List<Player> resultList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement getRankStatement = connection.prepareStatement(GET_ALL_RANKS_QUERY))
        {
            try (ResultSet results = getRankStatement.executeQuery())
            {
                while(results.next())
                {
                    resultList.add(
                            new Player(
                                    results.getString(1),
                                    results.getInt(2),
                                    0
                            )
                    );
                }
            }
        }
        catch (SQLException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "SQL error in getting current ranking; " + e.getMessage());
        }

        return resultList;
    }

    /**
     * Change the rank of a player.
     * @param player The player who's rank should be changed.
     * @param newRanking The new rank of the player.
     */
    public void changePlayerRankTo(Player player, int newRanking)
    {
        if (player.getSessionID() == 0) return;

        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement rankUpdateStatement = connection.prepareStatement(CHANGE_RANK_QUERY))
        {
            rankUpdateStatement.setString(1, Integer.toString(newRanking));
            rankUpdateStatement.setString(2, player.getUsername());

            rankUpdateStatement.execute();
        }
        catch (SQLException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "SQL error in changing the rank of a player; " + e.getMessage());
        }
    }

    /**
     * Get the rank of a specific player.
     * @param username Username of that player.
     * @return The rank of the specified player. If there was no rank found return the minimum value of an Integer.
     */
    public int getPlayerRank(String username)
    {
        int playerRank = Integer.MIN_VALUE;
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement getRankStatement = connection.prepareStatement(GET_RANK_QUERY))
        {
            getRankStatement.setString(1, username);

            try (ResultSet results = getRankStatement.executeQuery())
            {
                while(results.next())
                {
                    playerRank = results.getInt(1);
                }
            }
        }
        catch (SQLException e)
        {
            DEBUG_LOGGER.log(Level.SEVERE, "SQL error in getting the rank of a player; " + e.getMessage());
        }

        return playerRank;
    }
}
