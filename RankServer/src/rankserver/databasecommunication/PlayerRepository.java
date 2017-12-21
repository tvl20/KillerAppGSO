package rankserver.databasecommunication;

import shared.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerRepository
{
    private String connectionString;

    private final String loginCheckQuery = "SELECT PlayerName FROM Players WHERE PlayerName = ? AND PlayerPassword = ?";
    private final String getRankQuery = "SELECT PlayerRank FROM Players WHERE PlayerName = ?";
    private final String changeRankQuery = "UPDATE Players SET PlayerRank = ? WHERE PlayerName = ?";
    private final String getAllRanksQuery = "SELECT PlayerName, PlayerRank FROM Players";

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
             PreparedStatement loginStatement = connection.prepareStatement(loginCheckQuery))
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
        List<Player> resultList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement getRankStatement = connection.prepareStatement(getAllRanksQuery))
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
            e.printStackTrace();
        }

        return resultList;
    }

    public void changePlayerRankTo(Player player, int newRanking)
    {
        if (player.getSessionID() == 0) return;

        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement rankUpdateStatement = connection.prepareStatement(changeRankQuery))
        {
            rankUpdateStatement.setString(1, Integer.toString(newRanking));
            rankUpdateStatement.setString(2, player.getUsername());

            rankUpdateStatement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public int getPlayerRank(String username)
    {
        int playerRank = 0;
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement getRankStatement = connection.prepareStatement(getRankQuery))
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
            e.printStackTrace();
        }

        return playerRank;
    }
}
