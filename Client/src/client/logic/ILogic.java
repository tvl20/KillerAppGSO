package client.logic;

import shared.Player;

import java.util.List;

public interface ILogic
{
    /**
     * Add a move to the board.
     * @param column The column to add your piece.
     * @return Whether the turn was successful or not.
     */
    boolean addMove(int column);

    /**
     * Try to login with specified username and password.
     * @param username Username for the login.
     * @param password Password for the login.
     * @return Whether the login was successful or not.
     */
    boolean logIn(String username, String password);

    /**
     * Try to register a new account with the specified username and password.
     * @param username Username for the new account.
     * @param password Password for the new account.
     * @return Whether the register was successful or not.
     */
    boolean register(String username, String password);

    /**
     * Join the matchmaking queue.
     */
    void joinMatch();

    /**
     * Resets the local Game object (after a match) so that a new game can be played.
     */
    void resetLocalGame();

    /**
     * Gets the current ranking from the ranking server
     * @return A list of player objects with their names and rank filled in.
     */
    List<Player> getCurrentRanking();
}
