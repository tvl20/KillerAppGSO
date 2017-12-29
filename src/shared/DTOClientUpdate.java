package shared;

import java.io.Serializable;

/**
 * Data Transfer Object that is used by the server to update the client(s).
 * This object is used to prevent any synchronisation issues.
 */
public class DTOClientUpdate implements Serializable
{
    private int columnLastTurn;
    private int rowLastTurn;
    private Player currentTurnPlayer;
    private Player victoriousPlayer;

    public int getColumnLastTurn()
    {
        return columnLastTurn;
    }

    public int getRowLastTurn()
    {
        return rowLastTurn;
    }

    public Player getCurrentTurnPlayer()
    {
        return currentTurnPlayer;
    }

    public Player getVictoriousPlayer()
    {
        return victoriousPlayer;
    }

    public DTOClientUpdate(int columnLastTurn, int rowLastTrun, Player currentTurnPlayer)
    {
        this(columnLastTurn, rowLastTrun, currentTurnPlayer, null);
    }

    public DTOClientUpdate(int columnLastTurn, int rowLastTrun, Player currentTurnPlayer, Player victoriousPlayer)
    {
        this.columnLastTurn = columnLastTurn;
        this.rowLastTurn = rowLastTrun;
        this.currentTurnPlayer = currentTurnPlayer;
        this.victoriousPlayer = victoriousPlayer;
    }

    @Override
    public String toString()
    {
        return "DTOClientUpdate{" +
                "columnLastTurn=" + columnLastTurn +
                ", currentTurnPlayer=" + currentTurnPlayer +
                ", victoriousPlayer=" + victoriousPlayer +
                '}';
    }
}
