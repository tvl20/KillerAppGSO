package rankserver.databasecommunication;

import shared.Player;

import java.util.List;

public interface IDatabase
{
    /**
     * Try to login with specified username and password.
     * @param username Username for the login.
     * @param password Password for the login.
     * @return Whether the login was successful or not.
     */
    int login(String username, String password);

    /**
     * Try to register a new account with the specified username and password.
     * @param username Username for the new account.
     * @param password Password for the new account.
     * @return Whether the register was successful or not.
     */
    boolean register(String username, String password);

    /**
     * Increase the rank of the specified player.
     * @param player The player who's rank should be increased.
     */
    void rankUpPlayer(Player player);

    /**
     * Decrease the rank of the specified player.
     * @param player The player who's rank should be decreased.
     */
    void rankDownPlayer(Player player);

    /**
     * Get the rank of the player specified.
     * @param username The player who's rank will be returned.
     * @return The rank of the player.
     */
    int getPlayerRank(String username);

    /**
     * Get the ranking of all the players.
     * @return The ranking of all the players.
     */
    List<Player> getCurrentRanking();
}
