package client.gui;

import client.logic.AppLogic;
import client.logic.ILogic;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientLauncher extends Application
{
    private ILogic appLogic;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ClientLauncher application = new ClientLauncher();
        application.appLogic = new AppLogic();
    }
}
