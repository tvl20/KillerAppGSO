package shared;

import java.io.Serializable;

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
