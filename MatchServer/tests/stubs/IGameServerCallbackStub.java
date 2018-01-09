package stubs;

import matchserver.IGameServerCallback;
import matchserver.Match;

public class IGameServerCallbackStub implements IGameServerCallback
{
    private boolean gameFinishedCalled;

    public boolean isGameFinishedCalled()
    {
        return gameFinishedCalled;
    }

    @Override
    public void matchFinished(Match match)
    {
        gameFinishedCalled = true;
    }
}
