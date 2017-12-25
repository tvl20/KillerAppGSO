package client.gui;

import client.logic.AppLogic;
import client.logic.ILogic;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientLauncher extends Application implements IGUI
{
    private boolean started = false;
    private boolean loggedIn = false;

    private final Color clientColor = Color.YELLOW;
    private final Color opponentColor = Color.RED;

    private ILogic appLogic;

    private final int panelWidth = 500;
    private final int panelHeight = 500;
    private final double panelGridWidth = (double) panelWidth / 7;
    private final double panelGridHeight = (double) panelHeight / 6;
    private Canvas boardPanel;

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
    private final int colorSquareWidth = 47;
    private final int colorSquareHeight = 47;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Create the logic of the application
        appLogic = new AppLogic(this);
        System.out.println(appLogic);


        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        //Debug lines
//        grid.setGridLinesVisible(true);


        // Create the board panel element
        boardPanel = new Canvas(panelWidth, panelHeight);
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
        grid.add(playKeyButton, 2, 1, 1,1);
        playKeyButton.setVisible(false);


        // To display who's turn it is
        currentPlayerLabel = new Label("Current turn: ");
        grid.add(currentPlayerLabel, 5,1,1,1);
        currentPlayerLabel.setVisible(false);

        currentPlayerColor = new Canvas(colorSquareWidth, colorSquareHeight);
        grid.add(currentPlayerColor, 6, 1,1, 1);
        currentPlayerColor.setVisible(false);


        // To notify the player what his/her color is
        localPlayerLabel = new Label("Your Color: ");
        grid.add(localPlayerLabel, 5, 2, 1,1);
        localPlayerLabel.setVisible(false);

        localPlayerColor = new Canvas(colorSquareWidth, colorSquareHeight);
        grid.add(localPlayerColor, 6, 2,1,1);

        GraphicsContext gc = localPlayerColor.getGraphicsContext2D();
        gc.setFill(clientColor);
        gc.fillRect(0,0,colorSquareWidth,colorSquareHeight);
        localPlayerColor.setVisible(false);


        // Create the Login username elements
        usernameLabel = new Label("Username: ");
        grid.add(usernameLabel, 1,1,1,1);

        usernameField = new TextField();
        grid.add(usernameField, 2,1,1,1);


        // Create the Login password elements
        passwordLabel = new Label("Password: ");
        grid.add(passwordLabel, 1,2,1,1);

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
        grid.add(joinGameButton, 1,1,1,1);
        joinGameButton.setVisible(false);


        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, panelWidth + 50, panelHeight + 330);
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

        if (color.equals(clientColor))
        {
            setCurrentPlayerColor(opponentColor);
        }
        else
        {
            setCurrentPlayerColor(clientColor);
        }

        GraphicsContext gc = boardPanel.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(panelGridWidth * x, panelGridHeight * (6 - y), panelGridWidth, panelGridHeight);

        drawGrid();
    }

    @Override
    public void startGame(boolean starting)
    {
        if (joinGameButton.isVisible())
        {
            joinGameButton.setText("Join Game");
            joinGameButton.setVisible(false);
        }

        if (!boardPanel.isVisible())
        {
            boardPanel.setVisible(true);
            playKeyColumnComboBox.setVisible(true);
            playKeyButton.setVisible(true);
            currentPlayerColor.setVisible(true);
            currentPlayerLabel.setVisible(true);
            localPlayerColor.setVisible(true);
            localPlayerLabel.setVisible(true);
        }

        GraphicsContext gc = currentPlayerColor.getGraphicsContext2D();
        if (starting)
        {
            gc.setFill(clientColor);
        }
        else
        {
            gc.setFill(opponentColor);
        }
        gc.fillRect(0, 0, colorSquareWidth, colorSquareHeight);

        drawGrid();
    }

    private void clearPanel()
    {
        GraphicsContext gc = boardPanel.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, panelWidth, panelHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, panelWidth, panelHeight);
    }

    private void drawGrid()
    {
        GraphicsContext gc = boardPanel.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.setFill(Color.BLUE);

        for (int i = 0; i < 7; i++)
        {
            gc.fillText(Integer.toString(i + 1), (panelGridWidth * i) + (panelGridWidth * 0.5), panelHeight);
            gc.strokeLine(panelGridWidth * i, 0, panelGridWidth * i, panelHeight);
        }

        for (int i = 0; i < 6; i++)
        {
            gc.strokeLine(0, panelGridHeight * i, panelWidth, panelGridHeight * i);
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
            usernameField.setVisible(false);
            passwordField.setVisible(false);
            usernameLabel.setVisible(false);
            passwordLabel.setVisible(false);
            logInButton.setVisible(false);
            registerButton.setVisible(false);

            joinGameButton.setVisible(true);
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

    private void setCurrentPlayerColor(Color playerColor)
    {
        GraphicsContext gc = currentPlayerColor.getGraphicsContext2D();
        gc.setFill(playerColor);
        gc.fillRect(0,0,colorSquareWidth,colorSquareHeight);
    }
}
