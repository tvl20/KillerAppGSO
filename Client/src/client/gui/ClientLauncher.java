package client.gui;

import client.logic.AppLogic;
import client.logic.ILogic;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import shared.Player;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class keeps track of all the GUI elements and handling the GUI
 */
public class ClientLauncher extends Application implements IGUI
{
    private static final Logger DEBUG_LOGGER = Logger.getLogger("debugLogger");

    private boolean started = false;
    private boolean loggedIn = false;

    private static final Color CLIENT_COLOR = Color.YELLOW;
    private static final Color OPPONENT_COLOR = Color.RED;

    private ILogic appLogic;

    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 500;
    private static final double PANEL_GRID_WIDTH = (double) PANEL_WIDTH / 7;
    private static final double PANEL_GRID_HEIGHT = (double) PANEL_HEIGHT / 6;
    private Canvas boardPanel;

    private Label scoreBoardLabel;
    private ListView<Player> scoreBoardList;
    private Button scoreBoardRefreshButton;

    private Label usernameLabel;
    private TextField usernameField;
    private Label passwordLabel;
    private TextField passwordField;
    private Button logInButton;
    private Button registerButton;

    private Button joinGameButton;
    private Button playKeyButton;
    private ComboBox<Integer> playKeyColumnComboBox;

    private Label currentPlayerLabel;
    private Canvas currentPlayerColor;
    private Label localPlayerLabel;
    private Canvas localPlayerColor;
    private static final int COLOR_SQUARE_WIDTH = 47;
    private static final int COLOR_SQUARE_HEIGHT = 47;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Create the logic of the application
        appLogic = new AppLogic(this);
        DEBUG_LOGGER.log(Level.INFO, "App logic created");


        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        // Create the board panel element
        boardPanel = new Canvas(PANEL_WIDTH, PANEL_HEIGHT);
        grid.add(boardPanel, 0, 4, 25, 11);

        clearPanel();
        boardPanel.setVisible(false);


        // Combo box with possible columns to put a key into
        playKeyColumnComboBox = new ComboBox<>();
        playKeyColumnComboBox.setItems(FXCollections.observableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
        grid.add(playKeyColumnComboBox, 1, 1, 1, 1);
        playKeyColumnComboBox.setVisible(false);

        playKeyButton = new Button("Play Key");
        playKeyButton.setOnAction(event -> playKeyButtonHandler());
        grid.add(playKeyButton, 2, 1, 1, 1);
        playKeyButton.setVisible(false);


        // To display who's turn it is
        currentPlayerLabel = new Label("Current turn: ");
        grid.add(currentPlayerLabel, 5, 1, 1, 1);
        currentPlayerLabel.setVisible(false);

        currentPlayerColor = new Canvas(COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);
        grid.add(currentPlayerColor, 6, 1, 1, 1);
        currentPlayerColor.setVisible(false);


        // To notify the player what his/her color is
        localPlayerLabel = new Label("Your Color: ");
        grid.add(localPlayerLabel, 5, 2, 1, 1);
        localPlayerLabel.setVisible(false);

        localPlayerColor = new Canvas(COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);
        grid.add(localPlayerColor, 6, 2, 1, 1);

        GraphicsContext gc = localPlayerColor.getGraphicsContext2D();
        gc.setFill(CLIENT_COLOR);
        gc.fillRect(0, 0, COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);
        localPlayerColor.setVisible(false);


        // Create the Login username elements
        usernameLabel = new Label("Username: ");
        grid.add(usernameLabel, 1, 1, 1, 1);

        usernameField = new TextField();
        grid.add(usernameField, 2, 1, 1, 1);


        // Create the Login password elements
        passwordLabel = new Label("Password: ");
        grid.add(passwordLabel, 1, 2, 1, 1);

        passwordField = new TextField();
        grid.add(passwordField, 2, 2, 1, 1);


        // Create login button
        logInButton = new Button("Log In");
        logInButton.setOnAction(event -> logInButtonHandler());
        grid.add(logInButton, 3, 1, 1, 1);


        // Create register button
        registerButton = new Button("Register");
        registerButton.setOnAction(event -> registerHandler());
        grid.add(registerButton, 3, 2, 1, 1);


        // Create join game button
        joinGameButton = new Button("Join game");
        joinGameButton.setOnAction(event -> joinGameButtonHandler());
        grid.add(joinGameButton, 1, 1, 1, 1);
        joinGameButton.setVisible(false);


        // Create the scoreboard elements
        scoreBoardLabel = new Label("Scoreboard: (Username - Ranking)");
        grid.add(scoreBoardLabel, 1, 3, 1, 1);
        scoreBoardLabel.setVisible(false);

        scoreBoardRefreshButton = new Button("Update scoreboard");
        scoreBoardRefreshButton.setOnAction(event -> updateScoreBoard());
        grid.add(scoreBoardRefreshButton, 1, 5, 1, 1);
        scoreBoardRefreshButton.setVisible(false);

        scoreBoardList = new ListView<>();
        grid.add(scoreBoardList, 1,4, 3, 10);
        scoreBoardList.setVisible(false);


        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, PANEL_WIDTH + 50, PANEL_HEIGHT + 330);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void drawKey(int x, int y, Color color)
    {
        if (!started || !loggedIn)
        {
            return;
        }

        if (color.equals(CLIENT_COLOR))
        {
            setCurrentPlayerColor(OPPONENT_COLOR);
        }
        else
        {
            setCurrentPlayerColor(CLIENT_COLOR);
        }

        GraphicsContext gc = boardPanel.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(PANEL_GRID_WIDTH * x, PANEL_GRID_HEIGHT * (6 - y), PANEL_GRID_WIDTH, PANEL_GRID_HEIGHT);

        drawGrid();
    }

    @Override
    public void startGame(boolean starting)
    {
        clearPanel();

        scoreBoardLabel.setVisible(false);
        scoreBoardList.setVisible(false);
        scoreBoardRefreshButton.setVisible(false);

        joinGameButton.setText("Join Game");
        joinGameButton.setVisible(false);

        boardPanel.setVisible(true);
        playKeyColumnComboBox.setVisible(true);
        playKeyButton.setVisible(true);
        currentPlayerColor.setVisible(true);
        currentPlayerLabel.setVisible(true);
        localPlayerColor.setVisible(true);
        localPlayerLabel.setVisible(true);

        GraphicsContext gc = currentPlayerColor.getGraphicsContext2D();
        if (starting)
        {
            gc.setFill(CLIENT_COLOR);
        }
        else
        {
            gc.setFill(OPPONENT_COLOR);
        }
        gc.fillRect(0, 0, COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);

        drawGrid();
    }

    @Override
    public void won()
    {
        // Give the user a notification that he/she has won and reset the logic
        Alert wonAlert = new Alert(Alert.AlertType.CONFIRMATION);
        wonAlert.setTitle("You won");
        wonAlert.show();
        appLogic.resetLocalGame();
        started = false;
        resetUI();
    }

    @Override
    public void lost()
    {
        // Give the user a notification that he/she has lost and reset the logic
        Alert wonAlert = new Alert(Alert.AlertType.CONFIRMATION);
        wonAlert.setTitle("You lost");
        wonAlert.show();
        appLogic.resetLocalGame();
        started = false;
        resetUI();
    }

    /**
     * Clear the UI to just be the Join Game button and the scoreboard
     */
    private void resetUI()
    {
        usernameField.setVisible(false);
        passwordField.setVisible(false);
        usernameLabel.setVisible(false);
        passwordLabel.setVisible(false);
        logInButton.setVisible(false);
        registerButton.setVisible(false);

        playKeyColumnComboBox.setVisible(false);
        playKeyButton.setVisible(false);
        currentPlayerColor.setVisible(false);
        currentPlayerLabel.setVisible(false);
        localPlayerColor.setVisible(false);
        localPlayerLabel.setVisible(false);
        boardPanel.setVisible(false);

        joinGameButton.setVisible(true);
        joinGameButton.setDisable(false);

        scoreBoardLabel.setVisible(true);
        scoreBoardList.setVisible(true);
        scoreBoardRefreshButton.setVisible(true);
        updateScoreBoard();
    }

    private void updateScoreBoard()
    {
        scoreBoardList.setItems(FXCollections.observableList(appLogic.getCurrentRanking()));
    }

    /**
     * Clears the board canvas
     */
    private void clearPanel()
    {
        GraphicsContext gc = boardPanel.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, PANEL_WIDTH, PANEL_HEIGHT);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, PANEL_WIDTH, PANEL_HEIGHT);
    }

    /**
     * Draws a grid where the keys will end up getting put into
     */
    private void drawGrid()
    {
        GraphicsContext gc = boardPanel.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.setFill(Color.BLUE);

        for (int i = 0; i < 7; i++)
        {
            gc.fillText(Integer.toString(i + 1), (PANEL_GRID_WIDTH * i) + (PANEL_GRID_WIDTH * 0.5), PANEL_HEIGHT);
            gc.strokeLine(PANEL_GRID_WIDTH * i, 0, PANEL_GRID_WIDTH * i, PANEL_HEIGHT);
        }

        for (int i = 0; i < 6; i++)
        {
            gc.strokeLine(0, PANEL_GRID_HEIGHT * i, PANEL_WIDTH, PANEL_GRID_HEIGHT * i);
        }
    }

    private void joinGameButtonHandler()
    {
        if (started || !loggedIn)
        {
            return;
        }

        started = true;
        joinGameButton.setText("Joining Game...");
        joinGameButton.setDisable(true);

        appLogic.joinMatch();
    }

    private void logInButtonHandler()
    {
        if (started || loggedIn)
        {
            return;
        }

        String name = usernameField.getText();
        String pass = passwordField.getText();

        if (name == null)
        {
            name = "";
        }

        if (pass == null)
        {
            pass = "";
        }

        loggedIn = appLogic.logIn(name, pass);

        if (loggedIn)
        {
            resetUI();
        }
        else
        {
            Alert invalidMoveAlert = new Alert(Alert.AlertType.ERROR);
            invalidMoveAlert.setTitle("Invalid credentials, try again");
            invalidMoveAlert.show();
        }
    }

    private void registerHandler()
    {
        if (started || loggedIn)
        {
            return;
        }

        String name = usernameField.getText();
        String pass = passwordField.getText();

        if (name == null)
        {
            name = "";
        }

        if (pass == null)
        {
            pass = "";
        }

        boolean success = appLogic.register(name, pass);

        if (success)
        {
            Alert successfulAccountCreation = new Alert(Alert.AlertType.CONFIRMATION);
            successfulAccountCreation.setTitle("Account created");
            successfulAccountCreation.show();
        }
        else
        {
            Alert failedCreatingAccount = new Alert(Alert.AlertType.ERROR);
            failedCreatingAccount.setTitle("Unable to create account, username already registered");
            failedCreatingAccount.show();
        }
    }

    private void playKeyButtonHandler()
    {
        int column = playKeyColumnComboBox.getValue();
        column--;
        boolean success = appLogic.addMove(column);

        if (!success)
        {
            Alert invalidMoveAlert = new Alert(Alert.AlertType.ERROR);
            invalidMoveAlert.setTitle("Invalid Move!");
            invalidMoveAlert.show();
        }
    }

    /**
     * Fill the square with the color of the player who's turn it is.
     * @param playerColor The color of the player who's turn it is.
     */
    private void setCurrentPlayerColor(Color playerColor)
    {
        GraphicsContext gc = currentPlayerColor.getGraphicsContext2D();
        gc.setFill(playerColor);
        gc.fillRect(0, 0, COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);
    }
}
