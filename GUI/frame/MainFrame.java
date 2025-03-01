package GUI.frame;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainFrame {

    private BorderPane root;
    private Scene scene;
    private Stage stage;

    public MainFrame(Stage stage) {
        this.stage = stage;
        root = new BorderPane();
        scene = new Scene(root, 1000, 600);

        // showLogin();
        showMainApp();
    }

    public void showLogin() {
        LoginView loginView = new LoginView(this);
        root.setCenter(loginView.getView());

        // Cambia il CSS per il login
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/GUI/resources/style/app.css").toExternalForm());
    }

    public void showMainApp() {
        GenericFrame genericFrame = new GenericFrame(this);
        root.setCenter(genericFrame.getView());

        // Cambia il CSS per l'app principale
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/GUI/resources/style/app.css").toExternalForm());
    }

    public void showChangePassword() {
        ChangePasswordView cpView = new ChangePasswordView(this);
        root.setCenter(cpView.getView());

        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/GUI/resources/style/app.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }
}