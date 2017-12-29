package shared;

import java.io.Serializable;

/**
 * This class represents a player.
 * This class is also used by the ranking server to create a ranking list of all the players in the database.
 */
public class Player implements Serializable
{
    private String username;
    private int ranking;
    private int sessionID;

    public String getUsername()
    {
        return username;
    }

    public int getRanking()
    {
        return ranking;
    }

    public int getSessionID()
    {
        return sessionID;
    }

    public Player(String username, int ranking, int sessionID)
    {
        this.username = username;
        this.ranking = ranking;
        this.sessionID = sessionID;
    }

    @Override
    public String toString()
    {
        return getUsername() + " - " + Integer.toString(getRanking());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Player player = (Player) o;

        return getSessionID() == player.getSessionID() && getUsername().equals(player.getUsername());
    }

    @Override
    public int hashCode()
    {
        int result = getUsername().hashCode();
        result = 31 * result + getSessionID();
        return result;
    }
}
