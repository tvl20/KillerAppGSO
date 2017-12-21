package shared;

public class DTOClientUpdate
{
    private int columnLastTurn;
    private Player currentTurnPlayer;
    private Player victoriousPlayer;

    public int getColumnLastTurn()
    {
        return columnLastTurn;
    }

    public Player getCurrentTurnPlayer()
    {
        return currentTurnPlayer;
    }

    public Player getVictoriousPlayer()
    {
        return victoriousPlayer;
    }

    public DTOClientUpdate(int columnLastTurn, Player currentTurnPlayer)
    {
        this(columnLastTurn, currentTurnPlayer, null);
    }

    public DTOClientUpdate(int columnLastTurn, Player currentTurnPlayer, Player victoriousPlayer)
    {
        this.columnLastTurn = columnLastTurn;
        this.currentTurnPlayer = currentTurnPlayer;
        this.victoriousPlayer = victoriousPlayer;
    }
}
