package shared;

import java.io.Serializable;

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
}
