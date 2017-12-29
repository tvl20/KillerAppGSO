package client.gui;

import javafx.scene.paint.Color;

public interface IGUI
{
    /**
     * Draw a played key on the screen.
     * @param x X position of the key.
     * @param y Y position of the key.
     * @param color Color to draw the key in.
     */
    void drawKey(int x, int y, Color color);

    /**
     * Signal the GUI that a game has ended and a new one should be started.
     * @param starting Whether or not this client has to do the first move.
     */
    void startGame(boolean starting);

    /**
     * Signal the GUI that the player has won the game.
     */
    void won();

    /**
     * Signal the GUI that the player has lost the game.
     */
    void lost();
}
