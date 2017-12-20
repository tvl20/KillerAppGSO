package client.logic;

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
}
