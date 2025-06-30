package GUI.it.proj;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import GUI.it.proj.frame.GenericFrameController;
import GUI.it.proj.frame.LoginViewController;
import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Launcher extends Application {
    private static final double INITIAL_WIDTH = 1480;
    private static final double INITIAL_HEIGHT = 960;
    private static final String SETUP_DIALOG_TITLE = "Setup iniziale";
    private static final String ERROR_BORDER_STYLE = "error-border";

    private static Launcher instance = new Launcher();

    public Launcher(){
        if(controller == null){
            System.out.println("creo il controller");
            controller = new Controller(Model.getInstance());
        }
    }

    synchronized public static Launcher getInstance() {
        if (instance == null) {
            synchronized (Launcher.class){
                if (instance == null)
                    instance = new Launcher();
            }
        }
        return instance;
    }

    private Scene scene;
    public final Map<String, Parent> frames = new HashMap<>();
    public final Map<String, Initializable> controllers = new HashMap<>();
    public Controller controller;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        if (!getInstance().controller.isSetupCompleted()) {
            if (!showSetupDialog()) {
                // Platform.exit();
                // return;
            }
        }

        System.out.println("S");
        
        preloadFrames();
        setupPrimaryStage();
        openInterpreterWindow();
    }

    private void setupPrimaryStage() throws IOException {
        Parent initialRoot = getInstance().frames.get(LoginViewController.ID);
        if (initialRoot == null) {
            handleFatalError("Login frame non pre-caricato!");
            return;
        }

        scene = new Scene(initialRoot, INITIAL_WIDTH, INITIAL_HEIGHT);
        applyStylesheet(scene);

        positionStageOnSecondaryScreen();
        primaryStage.setScene(scene);
        primaryStage.setTitle("App visite guidate");
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Rimuovere per ambiente di produzione
        getInstance().controller.interpreter("login ADMIN PASSWORD");
        setRoot(GenericFrameController.ID);
    }

    private void preloadFrames() {
        loadFrame(GenericFrameController.ID, "generic-view");
        loadFrame(LoginViewController.ID, "login-view");
    }

    private boolean showSetupDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(SETUP_DIALOG_TITLE);

        TextField ambitoField = createInputField("Inserisci ambito territoriale");
        TextField maxField = createNumericField("Es. 100");
        
        HBox ambitoBox = createInputRow("Ambito Territoriale:", ambitoField);
        HBox maxBox = createInputRow("Numero massimo iscrizioni:", maxField);

        Button confirm = new Button("Conferma");
        confirm.setDefaultButton(true);
        confirm.setOnAction(e -> handleSetupConfirmation(ambitoField, maxField, dialog));

        VBox root = new VBox(15, ambitoBox, maxBox, confirm);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        setupDialogScene(dialog, root);
        dialog.showAndWait();
        return getInstance().controller.isSetupCompleted();
    }

    private void handleSetupConfirmation(TextField ambitoField, TextField maxField, Stage dialog) {
        String ambito = ambitoField.getText().trim();
        String maxTxt = maxField.getText().trim();
        
        boolean valid = validateInput(ambitoField, ambito.isEmpty());
        valid &= validateInput(maxField, maxTxt.isEmpty());
        
        if (!valid) return;
        
        try {
            int maxIscr = Integer.parseInt(maxTxt);
            Model.getInstance().appSettings.setAmbitoTerritoriale(ambito);
            Model.getInstance().appSettings.setMaxPrenotazioniPerPersona(maxIscr);
            getInstance().controller.skipSetup();
            dialog.close();
        } catch (NumberFormatException ex) {
            addErrorStyle(maxField);
        }
    }

    private boolean validateInput(TextField field, boolean isEmpty) {
        if (isEmpty) {
            addErrorStyle(field);
            return false;
        }
        return true;
    }

    private void addErrorStyle(TextField field) {
        if (!field.getStyleClass().contains(ERROR_BORDER_STYLE)) {
            field.getStyleClass().add(ERROR_BORDER_STYLE);
        }
    }

    private TextField createInputField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.textProperty().addListener((obs, oldVal, newVal) -> 
            field.getStyleClass().remove(ERROR_BORDER_STYLE)
        );
        return field;
    }

    private TextField createNumericField(String prompt) {
        TextField field = createInputField(prompt);
        field.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().matches("\\d*") ? change : null
        ));
        return field;
    }

    private HBox createInputRow(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setMinWidth(180);
        HBox row = new HBox(10, label, field);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void setupDialogScene(Stage dialog, VBox root) {
        Scene dialogScene = new Scene(root);
        applyStylesheet(dialogScene);
        dialog.setScene(dialogScene);
        dialog.setOnShown(evt -> centerDialog(dialog));
    }

    private void centerDialog(Stage dialog) {
        // Forza il calcolo delle dimensioni
        dialog.sizeToScene();
        
        // Ottieni i bounds dello schermo corrente
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        // Calcola le dimensioni del dialogo
        double dialogWidth = dialog.getWidth();
        double dialogHeight = dialog.getHeight();

        dialog.setWidth(dialogWidth * 1.1);
        dialog.setHeight(dialogHeight * 1.1);
        
        double centerX, centerY;
        
        if (primaryStage.isShowing()) {
            // Usa la posizione dello stage principale se visibile
            double ownerX = primaryStage.getX();
            double ownerY = primaryStage.getY();
            double ownerWidth = primaryStage.getWidth();
            double ownerHeight = primaryStage.getHeight();
            
            centerX = ownerX + (ownerWidth - dialogWidth) / 2;
            centerY = ownerY + (ownerHeight - dialogHeight) / 2;
        } else {
            // Centra sullo schermo primario
            centerX = (screenBounds.getWidth() - dialogWidth) / 2;
            centerY = (screenBounds.getHeight() - dialogHeight) / 2;
        }
        
        // Assicurati che il dialogo sia dentro lo schermo
        centerX = Math.max(screenBounds.getMinX(), 
                        Math.min(centerX, screenBounds.getMaxX() - dialogWidth));
        centerY = Math.max(screenBounds.getMinY(), 
                        Math.min(centerY, screenBounds.getMaxY() - dialogHeight));
        
        dialog.setX(centerX);
        dialog.setY(centerY);
    }

    private void positionStageOnSecondaryScreen() {
        Screen.getScreens().stream()
            .skip(1)
            .findFirst()
            .ifPresent(screen -> {
                Rectangle2D bounds = screen.getVisualBounds();
                primaryStage.setX(bounds.getMinX());
                primaryStage.setY(bounds.getMinY());
            });
    }

    private void applyStylesheet(Scene scene) {
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        URL cssUrl = getClass().getResource("/GUI/style/app.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    public void setRoot(String id) {
        Parent root = getInstance().frames.get(id);
        if (root != null && getInstance().scene != null) {
            System.out.println("imposto " + id);
            getInstance().scene.setRoot(root);
            if (getInstance().controllers.get(id) instanceof GenericFrameController frameController) {
                frameController.setupAfterLogin();
            }
        }
    }

    private void loadFrame(String id, String fxmlName) {
        try {
            URL fxmlUrl = getClass().getResource("/GUI/frame/" + fxmlName + ".fxml");
            if (fxmlUrl == null) {
                handleResourceError("FXML non trovato: /GUI/frame/" + fxmlName + ".fxml");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            getInstance().frames.put(id, root);
            getInstance().controllers.put(id, loader.getController());
        } catch (IOException e) {
            handleResourceError("Fallito caricamento FXML: " + fxmlName);
        }
    }

    private void handleFatalError(String message) {
        System.err.println("FATAL ERROR: " + message);
        getInstance().controller.interpreter("exit");
        Platform.exit();
    }

    private void handleResourceError(String message) {
        System.err.println("RESOURCE ERROR: " + message);
    }

    private void openInterpreterWindow() {
        Stage interpreterStage = new Stage();
        interpreterStage.setTitle("Interprete comandi");

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);

        TextField input = new TextField();
        input.setPromptText("Scrivi comando...");

        Button sendButton = new Button("Invia");
        sendButton.setOnAction(e -> executeCommand(input, output));

        VBox layout = new VBox(10, output, input, sendButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 500, 400);
        applyStylesheet(scene);
        interpreterStage.setScene(scene);
        interpreterStage.show();
    }

    private void executeCommand(TextField input, TextArea output) {
        String command = input.getText().trim();
        if (!command.isEmpty()) {
            Payload<?> result = getInstance().controller.interpreter(command);
            output.appendText("> " + command + "\n" + result + "\n\n");
            input.clear();
        }
    }

    public void toast(Payload<?> p) {
        if (p != null && getInstance().controllers.get(GenericFrameController.ID) instanceof GenericFrameController gf) {
            gf.toast(p);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}