package matchserver;

public interface IGameServerCallback
{
    /**
     * Signal to the match has been concluded and no longer has to be tracked.
     * @param match The match that has been finished.
     */
    void matchFinished(Match match);
}
