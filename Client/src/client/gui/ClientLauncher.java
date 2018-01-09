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
import java.util.Comparator;
import java.util.List;
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

    private GridPane grid;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Create the logic of the application
        appLogic = new AppLogic(this);
        DEBUG_LOGGER.log(Level.INFO, "App logic created");

        // Define grid pane
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Create the board panel element
        boardPanel = new Canvas(PANEL_WIDTH, PANEL_HEIGHT);
        grid.add(boardPanel, 0, 4, 25, 11);

        clearPanel();
        boardPanel.setVisible(false);
        boardPanel.setManaged(false);


        // Combo box with possible columns to put a key into
        playKeyColumnComboBox = new ComboBox<>();
        playKeyColumnComboBox.setItems(FXCollections.observableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
        grid.add(playKeyColumnComboBox, 1, 1, 1, 1);
        playKeyColumnComboBox.setVisible(false);
        playKeyColumnComboBox.setManaged(false);

        playKeyButton = new Button("Play Key");
        playKeyButton.setOnAction(event -> playKeyButtonHandler());
        grid.add(playKeyButton, 2, 1, 1, 1);
        playKeyButton.setVisible(false);
        playKeyButton.setManaged(false);


        // To display who's turn it is
        currentPlayerLabel = new Label("Current turn: ");
        grid.add(currentPlayerLabel, 5, 1, 1, 1);
        currentPlayerLabel.setVisible(false);
        currentPlayerLabel.setManaged(false);

        currentPlayerColor = new Canvas(COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);
        grid.add(currentPlayerColor, 6, 1, 1, 1);
        currentPlayerColor.setVisible(false);
        currentPlayerColor.setManaged(false);


        // To notify the player what his/her color is
        localPlayerLabel = new Label("Your Color: ");
        grid.add(localPlayerLabel, 5, 2, 1, 1);
        localPlayerLabel.setVisible(false);
        localPlayerLabel.setManaged(false);

        localPlayerColor = new Canvas(COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);
        grid.add(localPlayerColor, 6, 2, 1, 1);

        GraphicsContext gc = localPlayerColor.getGraphicsContext2D();
        gc.setFill(CLIENT_COLOR);
        gc.fillRect(0, 0, COLOR_SQUARE_WIDTH, COLOR_SQUARE_HEIGHT);
        localPlayerColor.setVisible(false);
        localPlayerColor.setManaged(false);


        // Create the Login username elements
        usernameLabel = new Label("Username: ");
        grid.add(usernameLabel, 1, 1, 1, 1);

        usernameField = new TextField();
        grid.add(usernameField, 2, 1, 1, 1);


        // Create the Login password elements
        passwordLabel = new Label("Password: ");
        grid.add(passwordLabel, 1, 2, 1, 1);

        passwordField = new PasswordField();
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
        joinGameButton.setManaged(false);


        // Create the scoreboard elements
        scoreBoardLabel = new Label("Scoreboard: (Username - Ranking)");
        grid.add(scoreBoardLabel, 1, 3, 1, 1);
        scoreBoardLabel.setVisible(false);
        scoreBoardLabel.setManaged(false);

        scoreBoardRefreshButton = new Button("Update scoreboard");
        scoreBoardRefreshButton.setOnAction(event -> updateScoreBoard());
        grid.add(scoreBoardRefreshButton, 1, 2, 1, 1);
        scoreBoardRefreshButton.setVisible(false);
        scoreBoardRefreshButton.setManaged(false);

        scoreBoardList = new ListView<>();
        grid.add(scoreBoardList, 1,4, 3, 10);
        scoreBoardList.setVisible(false);
        scoreBoardList.setManaged(false);


        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, PANEL_WIDTH + 50, PANEL_HEIGHT + 175);
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
        scoreBoardLabel.setManaged(false);

        scoreBoardList.setVisible(false);
        scoreBoardList.setManaged(false);

        scoreBoardRefreshButton.setVisible(false);
        scoreBoardRefreshButton.setManaged(false);

        joinGameButton.setText("Join Game");
        joinGameButton.setVisible(false);
        joinGameButton.setManaged(false);

        boardPanel.setVisible(true);
        boardPanel.setManaged(true);

        playKeyColumnComboBox.setVisible(true);
        playKeyColumnComboBox.setManaged(true);

        playKeyButton.setVisible(true);
        playKeyButton.setManaged(true);

        currentPlayerColor.setVisible(true);
        currentPlayerColor.setManaged(true);

        currentPlayerLabel.setVisible(true);
        currentPlayerLabel.setManaged(true);

        localPlayerColor.setVisible(true);
        localPlayerColor.setManaged(true);

        localPlayerLabel.setVisible(true);
        localPlayerLabel.setManaged(true);

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
        wonAlert.setHeaderText("You won");
        wonAlert.show();
        appLogic.resetLocalGame();
        started = false;
        resetUI();
    }

    @Override
    public void lost()
    {
        // Give the user a notification that he/she has lost and reset the logic
        Alert lostAlert = new Alert(Alert.AlertType.CONFIRMATION);
        lostAlert.setTitle("You lost");
        lostAlert.setHeaderText("You lost");
        lostAlert.show();
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
        usernameField.setManaged(false);

        passwordField.setVisible(false);
        passwordField.setManaged(false);

        usernameLabel.setVisible(false);
        usernameLabel.setManaged(false);

        passwordLabel.setVisible(false);
        passwordLabel.setManaged(false);

        logInButton.setVisible(false);
        logInButton.setManaged(false);

        registerButton.setVisible(false);
        registerButton.setManaged(false);

        playKeyColumnComboBox.setVisible(false);
        playKeyColumnComboBox.setManaged(false);

        playKeyButton.setVisible(false);
        playKeyButton.setManaged(false);

        currentPlayerColor.setVisible(false);
        currentPlayerColor.setManaged(false);

        currentPlayerLabel.setVisible(false);
        currentPlayerLabel.setManaged(false);

        localPlayerColor.setVisible(false);
        localPlayerColor.setManaged(false);

        localPlayerLabel.setVisible(false);
        localPlayerLabel.setManaged(false);

        boardPanel.setVisible(false);
        boardPanel.setManaged(false);

        joinGameButton.setVisible(true);
        joinGameButton.setManaged(true);
        joinGameButton.setDisable(false);

        scoreBoardLabel.setVisible(true);
        scoreBoardLabel.setManaged(true);

        scoreBoardList.setVisible(true);
        scoreBoardList.setManaged(true);

        scoreBoardRefreshButton.setVisible(true);
        scoreBoardRefreshButton.setManaged(true);

        updateScoreBoard();
    }

    private void updateScoreBoard()
    {
        List<Player> sortedScoreList = appLogic.getCurrentRanking();
        sortedScoreList.sort(Comparator.comparingInt(Player::getRanking));
        scoreBoardList.setItems(FXCollections.observableList(sortedScoreList));
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
            invalidMoveAlert.setHeaderText("Invalid credentials, try again");
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

        boolean success = false;

        if (name != null && pass != null && !name.isEmpty() && !pass.isEmpty() && !name.contains(" ") && !pass.contains(" "))
        {
            success = appLogic.register(name, pass);
        }

        if (success)
        {
            Alert successfulAccountCreation = new Alert(Alert.AlertType.CONFIRMATION);
            successfulAccountCreation.setTitle("Account created");
            successfulAccountCreation.setHeaderText("Account created");
            successfulAccountCreation.show();
        }
        else
        {
            Alert failedCreatingAccount = new Alert(Alert.AlertType.ERROR);
            failedCreatingAccount.setTitle("Unable to create account, username already registered");
            failedCreatingAccount.setHeaderText("Unable to create account");
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
            invalidMoveAlert.setHeaderText("Invalid Move!");
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
