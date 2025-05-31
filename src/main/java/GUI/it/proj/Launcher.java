package GUI.it.proj;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader; // Importa FXMLLoader
import javafx.fxml.Initializable; // Importa Initializable
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import GUI.it.proj.frame.GenericFrameController;
import GUI.it.proj.frame.LoginViewController;
import GUI.it.proj.utils.Loader; // Potresti non averne più bisogno per caricare i frame principali
import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;

import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map; // Usa Map invece di HashMap per le dichiarazioni

public class Launcher extends Application {
    private static Scene scene;
    private static Map<String, Parent> frames = new HashMap<>();
    // Mappa per tenere i controller associati ai frame principali pre-caricati
    private static Map<String, Initializable> controllers = new HashMap<>();


    @Override
    public void start(Stage stage) throws IOException {
        // Imposta la scena iniziale sul frame di login
        Parent initialRoot = frames.get(LoginViewController.ID);

        
        if (initialRoot == null) {
            System.err.println("FATAL ERROR: Login frame not pre-loaded!");
            // Potresti voler uscire o gestire l'errore
            controller.interpreter("exit");
            Platform.exit();
            return;
        }
        
        scene = new Scene(initialRoot, 1480, 960);

        var screens = Screen.getScreens();
        if (screens.size() > 1) {
            Screen secondScreen = screens.get(1);
            Rectangle2D bounds = secondScreen.getVisualBounds();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
        }
        
        
        stage.setScene(scene);
        stage.setTitle("App visite guidate");
        
        // Prima pulisci e aggiungi BootstrapFX
        scene.getStylesheets().clear();
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        
        // Poi carichi il tuo CSS qui
        URL cssUrl = Launcher.class.getResource("/GUI/style/app.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            // WARNING: Assicurati che il lesscss-maven-plugin compili in /GUI/style/app.css
            System.err.println("WARNING: /GUI/style/app.css non trovato nel classpath.");
        }
        
        stage.centerOnScreen();
        stage.setMaximized(true);
        stage.show();
        
        // Non chiamare setupAfterLogin o showHome qui.
        // Questo avviene solo quando il GenericFrame viene impostato come root DOPO il login.
        controller.interpreter("login ADMIN PASSWORD");
        setRoot(GenericFrameController.ID);
        openInterpreterWindow();
    }

    /**
     * Imposta un frame pre-caricato come root della scena.
     * Chiama setupAfterLogin sul controller del frame se appropriato.
     * @param id L'ID del frame (chiave nella mappa 'frames').
     */
    public static void setRoot(String id) {
        Parent root = frames.get(id);
        if (root != null) {
            scene.setRoot(root);

            // Ottieni il controller associato
            Initializable controllerView = controllers.get(id);

            // Se il controller è GenericFrameController, chiama il suo metodo di setup post-login
            if (controllerView instanceof GenericFrameController) {
                ((GenericFrameController) controllerView).setupAfterLogin();
            }
            // Puoi aggiungere altri 'else if' qui per altri tipi di controller
            // che potrebbero aver bisogno di setup quando vengono mostrati

        } else {
            System.err.println("Error: FXML frame '" + id + "' not found in pre-loaded frames.");
            // Potresti voler mostrare uno stato di errore o tornare al login
            // Esempio: setRoot(LoginViewController.ID);
        }
    }

    // Metodo originale setRoot con clear (per ricaricare FXML, se necessario) - Lascialo se lo usi altrove
    // Nota: questo metodo *non* userà i frame pre-caricati e ricreerà l'UI e il controller
    // ogni volta che viene chiamato.
    public static void setRoot(String fxmlFileName, boolean clear) {
        // Questa versione ricarica sempre l'FXML
        Parent root = Loader.loadFXML(String.format("%s-view", fxmlFileName)); // Assumi che Loader.loadFXML ritorni solo Parent
        if (root != null) {
            scene.setRoot(root);
            System.out.println("WARNING: setRoot(..., true) does not automatically call setupAfterLogin.");
        } else {
            System.err.println("Error: Failed to load FXML file '" + fxmlFileName + "-view.fxml'");
        }
    }


    public static Controller controller;

    public static void main(String[] args) {
        // Inizializza il modello e il controller PRIMA di caricare gli FXML
        controller = new Controller();
        
        // Pre-carica i frame principali e i loro controller
        // Usa un helper locale per caricare e memorizzare sia il Parent che il Controller
        storeFrame(GenericFrameController.ID, "generic-view");
        storeFrame(LoginViewController.ID, "login-view");

        // Avvia l'applicazione JavaFX
        launch();
        // System.exit(0);
    }

    /**
     * Helper per caricare un FXML, memorizzare il Parent e il Controller associato.
     */
    private static void storeFrame(String id, String fxmlFileName) {
        try {
            URL fxmlUrl = Launcher.class.getResource("/GUI/frame/" + fxmlFileName + ".fxml");
            if (fxmlUrl == null) {
                 System.err.println("FATAL ERROR: FXML file not found: /GUI/frame/" + fxmlFileName + ".fxml");
                 // Puoi lanciare un'eccezione o uscire
                 return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Initializable ctrl = loader.getController(); // Ottieni il controller

            frames.put(id, root);
            controllers.put(id, ctrl); // Memorizza il controller

            System.out.println("Pre-loaded frame: " + id + " with controller: " + (ctrl != null ? ctrl.getClass().getSimpleName() : "None"));

        } catch (IOException e) {
            System.err.println("FATAL ERROR: Failed to load FXML: " + fxmlFileName);
            e.printStackTrace();
            // Puoi lanciare un'eccezione o uscire
        } catch (Exception e) {
             System.err.println("FATAL ERROR: An unexpected error occurred while loading FXML: " + fxmlFileName);
             e.printStackTrace();
        }
    }

    // Potresti voler esporre i controller se necessario (ad es. per chiamare metodi specifici su un controller)
    public static <T extends Initializable> T getController(String id) {
         // Esegui un cast sicuro se conosci il tipo atteso
         return (T) controllers.get(id);
    }

    public static void toast(Payload<?> p){
        if(p == null)
            return;
        ((GenericFrameController) getController(GenericFrameController.ID)).toast(p);
    }

    private void openInterpreterWindow() {
        Stage interpreterStage = new Stage();
        interpreterStage.setTitle("Interprete comandi");

        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);

        TextField input = new TextField();
        input.setPromptText("Scrivi comando...");

        Button sendButton = new Button("Invia");
        sendButton.setOnAction(e -> {
            String command = input.getText();
            if (!command.isBlank()) {
                Payload<?> result = controller.interpreter(command);
                output.appendText("> " + command + "\n" + result + "\n\n");
                input.clear();
            }
        });

        input.setOnAction(sendButton.getOnAction()); // Invio con ENTER

        layout.getChildren().addAll(output, input, sendButton);
        Scene scene = new Scene(layout, 500, 400);
        interpreterStage.setScene(scene);
        interpreterStage.show();
    }

}