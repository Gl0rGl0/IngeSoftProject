package GUI;

import GUI.frame.MainFrame;
import javafx.application.Application;
import javafx.stage.Stage;

public class LauncherApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("App Visite Guidate");

        // Usa MainFrame per gestire le viste
        MainFrame mainFrame = new MainFrame(primaryStage);
        primaryStage.setScene(mainFrame.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}