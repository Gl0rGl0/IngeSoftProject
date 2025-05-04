package GUI.it.proj;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import GUI.it.proj.frame.GenericFrameController;
import GUI.it.proj.frame.LoginViewController;
import GUI.it.proj.utils.Loader;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class Launcher extends Application {
    private static Scene scene;
    private static HashMap<String, Parent> frames = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        // Carica i frame (già fatti in main)
        scene = new Scene(frames.get(GenericFrameController.ID), 1480, 960);
        stage.setScene(scene);
        stage.setTitle("App visite guidate");

        // Prima pulisci e aggiungi BootstrapFX
        scene.getStylesheets().clear();
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        // Poi carichi il tuo CSS qui, in modo sicuro
        URL cssUrl = Launcher.class.getResource("/GUI/style/test.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("WARNING: /GUI/style/test.css non trovato nel classpath");
        }

        stage.centerOnScreen();
        stage.show();
    }

    public static void setRoot(String fxml) {
        scene.setRoot(frames.get(fxml));
    }

    public static void setRoot(String fxml, boolean clear) {
        scene.setRoot(Loader.loadFXML(String.format("%s-view", fxml)));
    }

    public static void main(String[] args) {
        frames.put(GenericFrameController.ID, Loader.loadFXML("generic-view"));
        frames.put(LoginViewController.ID, Loader.loadFXML("login-view"));

        launch();
    }
}
