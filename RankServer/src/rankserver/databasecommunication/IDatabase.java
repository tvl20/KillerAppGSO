package rankserver.databasecommunication;

import shared.Player;

import java.util.List;

public interface IDatabase
{
    int login(String username, String password);
    boolean register(String username, String password);
    void rankUpPlayer(Player player);
    void rankDownPlayer(Player player);
    int getPlayerRank(String username);
    List<Player> getCurrentRanking();
}
