package rankserver.databasecommunication;

import shared.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class PlayerRepository
{
    /**
     * Check the credentials of a potential login
     * @param username Login username
     * @param password Login password
     * @return the new sessionID of the player, if the login was unsuccessful the returned value will be -1
     */
    public int logIn(String username, String password)
    {
        throw new NotImplementedException();
    }

    public boolean register(String username, String password)
    {
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
        throw new NotImplementedException();
    }
}
