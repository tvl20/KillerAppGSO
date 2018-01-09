package matchserver;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.Player;
import stubs.IGameServerCallbackStub;
import stubs.IGameStub;
import stubs.IRankingServerStub;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest
{
    private IGameStub game1;
    private IGameStub game2;
    private IRankingServerStub rankServer;
    private IGameServerCallbackStub gameServerCallback;

    private Match testMatch;

    @BeforeEach
    void setUp()
    {
        game1 = new IGameStub(new Player("testplayer1", 100, 1));
        game2 = new IGameStub(new Player("testplayer2", 200, 2));
        rankServer = new IRankingServerStub();
        gameServerCallback = new IGameServerCallbackStub();

        try
        {
            testMatch = new Match(game1, game2, rankServer, gameServerCallback);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }

        /*
        BOARD > int[][]

                 ROW 0  ROW 1  ROW 2
        COLUMN 0  X      X      X
        COLUMN 1  X      X      X
        COLUMN 2  X      X      X
         */
    }

    @Test
    void playerWonDiagonal1()
    {
        int[][] board =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 2, 1, 2, 1, 0},
                        {2, 1, 1, 1, 0, 0},
                        {2, 2, 1, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(board);

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal1LeftEdge()
    {
        int[][] leftEdgeBoard =
                {
                        {2, 2, 1, 2, 1, 0},
                        {2, 1, 1, 1, 0, 0},
                        {2, 2, 1, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(leftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal1RightEdge()
    {
        int[][] rightEdgeBoard =
                {
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 2, 1, 2, 1, 0},
                        {2, 1, 1, 1, 0, 0},
                        {2, 2, 1, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                };

        testMatch.setBoard(rightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal1BottomEdge()
    {
        int[][] bottomEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 1, 2, 1, 0, 0},
                        {1, 1, 1, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(bottomEdgeBoard);

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal1TopEdge()
    {
        int[][] topEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {1, 2, 2, 1, 2, 1},
                        {1, 2, 1, 1, 1, 0},
                        {2, 1, 2, 1, 0, 0},
                        {2, 1, 1, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(topEdgeBoard);

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal1TopLeftEdge()
    {
        int[][] topLeftEdgeBoard =
                {
                        {1, 2, 2, 1, 2, 1},
                        {1, 2, 1, 1, 1, 0},
                        {2, 1, 2, 1, 0, 0},
                        {2, 1, 1, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(topLeftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal1BottomRightEdge()
    {
        int[][] bottomRightEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 1, 2, 1, 0, 0},
                        {1, 1, 1, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {1, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(bottomRightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal2()
    {
        int[][] leftEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                        {2, 2, 1, 0, 0, 0},
                        {2, 1, 1, 1, 0, 0},
                        {2, 2, 1, 2, 1, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(leftEdgeBoard);

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal2LeftEdge()
    {
        int[][] leftEdgeBoard =
                {
                        {1, 1, 0, 0, 0, 0},
                        {2, 2, 1, 0, 0, 0},
                        {2, 1, 1, 1, 0, 0},
                        {2, 2, 1, 2, 1, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(leftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal2RightEdge()
    {
        int[][] rightEdgeBoard =
                {
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                        {2, 2, 1, 0, 0, 0},
                        {2, 1, 1, 1, 0, 0},
                        {2, 2, 1, 2, 1, 0},
                };

        testMatch.setBoard(rightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(5);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal2BottomEdge()
    {
        int[][] bottomEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {1, 0, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {1, 1, 1, 0, 0, 0},
                        {2, 1, 2, 1, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(bottomEdgeBoard);

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal2TopEdge()
    {
        int[][] topEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 1, 1, 0, 0, 0},
                        {2, 1, 2, 1, 0, 0},
                        {1, 2, 1, 1, 1, 0},
                        {1, 2, 2, 1, 2, 1},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(topEdgeBoard);

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal2TopRightEdge()
    {
        int[][] topRightEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 1, 1, 0, 0, 0},
                        {2, 1, 2, 1, 0, 0},
                        {1, 2, 1, 1, 1, 0},
                        {1, 2, 2, 1, 2, 1},
                };

        testMatch.setBoard(topRightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(5);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonDiagonal2BottomLeftEdge()
    {
        int[][] bottomLeftEdgeBoard =
                {
                        {1, 0, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {1, 1, 1, 0, 0, 0},
                        {2, 1, 2, 1, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };

        testMatch.setBoard(bottomLeftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(2);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonVertical()
    {
        int[][] leftEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 1, 1, 1, 1, 0},
                        {2, 2, 2, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(leftEdgeBoard);

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalLeftEdge()
    {
        int[][] leftEdgeBoard =
                {
                        {2, 1, 1, 1, 1, 0},
                        {2, 2, 2, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(leftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalRightEdge()
    {
        int[][] rightEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 2, 2, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 1, 1, 1, 1, 0},
                };
        testMatch.setBoard(rightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(4);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalBottomEdge()
    {
        int[][] bottomEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 2, 2, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 1, 1, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(bottomEdgeBoard);

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalTopEdge()
    {
        int[][] topEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 2, 1, 1, 1, 1},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(topEdgeBoard);

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalTopLeftEdge()
    {
        int[][] topLeftEdgeBoard =
                {
                        {2, 2, 1, 1, 1, 1},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(topLeftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalBottomLeftEdge()
    {
        int[][] bottomLeftEdgeBoard =
                {
                        {1, 1, 1, 1, 0, 0},
                        {2, 2, 2, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(bottomLeftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalTopRightEdge()
    {
        int[][] topRightEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 2, 1, 1, 1, 1},
                };
        testMatch.setBoard(topRightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonVerticalBottomRightEdge()
    {
        int[][] bottomRightEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 2, 2, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 1, 1, 0, 0},
                };
        testMatch.setBoard(bottomRightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(3);
        Assert.assertTrue(testMatch.playerWon());
    }

    @Test
    void playerWonHorizontal()
    {
        int[][] bottomEdgeBoard =
                {
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 1, 1, 0, 0, 0},
                        {1, 1, 2, 0, 0, 0},
                        {2, 1, 2, 0, 0, 0},
                        {1, 1, 2, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(bottomEdgeBoard);

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting a key in the middle of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(5);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalLeftEdge()
    {
        int[][] leftEdgeBoard =
                {
                        {1, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(leftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalRightEdge()
    {
        int[][] rightEdgeBoard =
                {
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 0},
                };
        testMatch.setBoard(rightEdgeBoard);

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(5);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key in the middle of a row", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(1);
        Assert.assertTrue("Error putting last key at the edge of the row", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalBottomEdge()
    {
        int[][] bottomEdgeBoard =
                {
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(bottomEdgeBoard);

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting a key in the middle of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(5);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalTopEdge()
    {
        int[][] topEdgeBoard =
                {
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 2, 2, 1, 1},
                        {2, 2, 1, 1, 2, 1},
                        {1, 1, 2, 2, 1, 1},
                        {2, 2, 1, 1, 2, 1},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(topEdgeBoard);

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting a key in the middle of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(5);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalTopLeftEdge()
    {
        int[][] topLeftEdgeBoard =
                {
                        {1, 1, 2, 2, 1, 1},
                        {2, 2, 1, 1, 2, 1},
                        {1, 1, 2, 2, 1, 1},
                        {2, 2, 1, 1, 2, 1},
                        {0, 0, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(topLeftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(1);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting a key in the middle of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalBottomLeftEdge()
    {
        int[][] bottomLeftEdgeBoard =
                {
                        {1, 1, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                };
        testMatch.setBoard(bottomLeftEdgeBoard);

        testMatch.setColumnLastTurn(0);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(2);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting a key in the middle of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalTopRightEdge()
    {
        int[][] topLeftEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 2, 2, 1, 1},
                        {2, 2, 1, 1, 2, 1},
                        {1, 1, 2, 2, 1, 1},
                        {2, 2, 1, 1, 2, 1},
                };
        testMatch.setBoard(topLeftEdgeBoard);

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(4);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting a key in the middle of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(5);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());
    }

    @Test
    void playerWonHorizontalBottomRightEdge()
    {
        int[][] bottomLeftEdgeBoard =
                {
                        {0, 0, 0, 0, 0, 0},
                        {2, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0},
                        {1, 1, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                        {1, 2, 0, 0, 0, 0},
                };
        testMatch.setBoard(bottomLeftEdgeBoard);

        testMatch.setColumnLastTurn(3);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(5);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting a key in the middle of the row and edge of the screen", testMatch.playerWon());

        testMatch.setColumnLastTurn(6);
        testMatch.setRowLastTurn(0);
        Assert.assertTrue("Error putting last key at the edge of the row and edge of the screen", testMatch.playerWon());
    }
}