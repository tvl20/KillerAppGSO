package rankserver.databasecommunication;

import shared.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.List;
import java.util.Random;

public class PlayerRepository
{
    private String connectionString;

    private final String LogincheckQuery = "SELECT PlayerName FROM Players WHERE PlayerName = ? AND PlayerPassword = ?";
    private final String GetRankQuery = "SELECT PlayerRank FROM Players WHERE PlayerName = ?";

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
            e.printStackTrace();
        }
    }

    /**
     * Check the credentials of a potential login
     * @param username Login username
     * @param password Login password
     * @return the new sessionID of the player, if the login was unsuccessful the returned value will be -1
     */
    public int logIn(String username, String password)
    {
        boolean successfulLogin = false;
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement loginStatement = connection.prepareStatement(LogincheckQuery))
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
            e.printStackTrace();
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

    public boolean register(String username, String password)
    {
        if (username.length() >= 256 || password.length() >= 64)
        {
            return false;
        }

        throw new NotImplementedException();
    }

    public List<Player> getCurrentRanking()
    {
        throw new NotImplementedException();
    }

    public void changePlayerRankTo(Player player, int newRanking)
    {
        throw new NotImplementedException();
    }

    public int getPlayerRank(String username)
    {
        int playerRank = 0;
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement loginStatement = connection.prepareStatement(GetRankQuery))
        {
            loginStatement.setString(1, username);

            try (ResultSet results = loginStatement.executeQuery())
            {
                while(results.next())
                {
                    playerRank = results.getInt(1);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return playerRank;
    }
}
