package rankserver.databasecommunication;

import shared.Player;

import java.util.List;

/**
 * Main class for the database communication.
 * This class is the class that keeps track of all the repositories and with those can access the database.
 */
public class DataLayer implements IDatabase
{
    private PlayerRepository playerRepo;

    public DataLayer()
    {
        playerRepo = new PlayerRepository();
    }

    @Override
    public int login(String username, String password)
    {
        return playerRepo.logIn(username, password);
    }

    @Override
    public boolean register(String username, String password)
    {
        return playerRepo.register(username, password);
    }

    @Override
    public void rankUpPlayer(Player player)
    {
        playerRepo.changePlayerRankTo(player, player.getRanking()+10);
    }

    @Override
    public void rankDownPlayer(Player player)
    {
        playerRepo.changePlayerRankTo(player, player.getRanking()-10);
    }

    @Override
    public int getPlayerRank(String username)
    {
        return playerRepo.getPlayerRank(username);
    }

    @Override
    public List<Player> getCurrentRanking()
    {
        return playerRepo.getCurrentRanking();
    }
}
