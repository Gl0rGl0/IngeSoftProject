package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Loader;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GenericFrameController implements Initializable {
    public final static String ID = "generic";

    // Mappa degli FXML caricati (esempio per change password, ecc.)
    // NOTA: Mantieni static se vuoi che sia popolata UNA SOLA VOLTA all'inizializzazione del primo GenericFrameController
    // Ma attenzione se crei più istanze di GenericFrameController
    // Visto come lo usi in Launcher, sembra che ci sia una sola istanza caricata all'inizio, quindi static va bene.
    private static final HashMap<String, Parent> contentFrames = new HashMap<>();

    // Elementi comuni
    @FXML
    private MenuButton userMenuButton;
    @FXML
    private StackPane contentArea;

    // Elementi specifici per ruolo
    @FXML
    private MenuButton databaseButton; // CONFIGURATORE

    @FXML
    private Button volontarioButton; // VOLONTARIO

    @FXML
    private Button prenotazioniButton; // FRUITORE

    @FXML private AnchorPane toastContainer;  // Overlay for toasts

    // Questa variabile conterrà il riferimento a questa istanza del controller,
    // utile per accedere a questo controller da metodi statici di Launcher.
    // MA con il nuovo approccio in Launcher.setRoot non è strettamente necessario.
    // private static GenericFrameController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentArea.setAlignment(Pos.CENTER);
    
        // Caricamenti FXML esistenti…
        contentFrames.put(HomeVisiteViewController.ID, Loader.loadFXML("home-view"));
        contentFrames.put(ChangePasswordViewController.ID, Loader.loadFXML("changepsw-view"));
    
        // Posiziona il toastContainer in alto a destra
        AnchorPane.setTopAnchor(toastContainer, 0.0);
        AnchorPane.setRightAnchor(toastContainer, 0.0);
        // Lo lasciamo parte del layout dello StackPane, non serve managed=false qui
    }

    /**
     * Metodo da chiamare dopo che il GenericFrame è stato impostato come root
     * della scena, ad esempio dopo un login di successo.
     * Esegue la configurazione iniziale della vista per l'utente corrente.
     */
    public void setupAfterLogin() {
        // Esegui qui le azioni che vuoi succedano ogni volta che il frame viene "mostrato" post-login
        System.out.println("GenericFrameController: Setup after login called.");

        //Refresh content
        
        switch (Launcher.controller.getCurrentUser().getType()) {
            case CONFIGURATORE -> {
                contentFrames.put(PersonViewController.ID, Loader.loadFXML("persone-view"));
                contentFrames.put(LuoghiVisiteViewController.ID, Loader.loadFXML("luoghi-visite-view"));
            }
            case VOLONTARIO -> contentFrames.put(VolontariViewController.ID, Loader.loadFXML("volontari-view"));
            case FRUITORE -> contentFrames.put(FruitoriViewController.ID, Loader.loadFXML("fruitori-view"));
            default -> {}
        }

        showHome(); // Chiama showHome() che a sua volta configurerà la navbar
    }


    /**
     * Configura la navbar in base al ruolo corrente.
     */
    private void configureNavbarForRole(PersonaType role) {
        System.out.println("Configuring navbar for role: " + role);
        // Nascondi tutti gli elementi specifici
        databaseButton.setVisible(false);
        databaseButton.setManaged(false);
        volontarioButton.setVisible(false);
        volontarioButton.setManaged(false);
        prenotazioniButton.setVisible(false);
        prenotazioniButton.setManaged(false);

        // Abilita gli elementi per il ruolo specifico
        switch (role) {
            case CONFIGURATORE:
                databaseButton.setVisible(true);
                databaseButton.setManaged(true);
                break;
            case VOLONTARIO:
                volontarioButton.setVisible(true);
                volontarioButton.setManaged(true);
                break;
            case FRUITORE:
                prenotazioniButton.setVisible(true);
                prenotazioniButton.setManaged(true);
                break;
            default:
                // Nessun elemento specifico abilitato
                System.err.println("WARNING: Unhandled PersonaType for navbar configuration: " + role);
                break;
        }

        // Aggiorna il testo dell'utente nella navbar
        // Assicurati che Launcher.controller.getCurrentUser() non sia null qui
        if (Launcher.controller != null && Launcher.controller.getCurrentUser() != null) {
             userMenuButton.setText(Launcher.controller.getCurrentUser().getUsername());
        } else {
            userMenuButton.setText("Utente Sconosciuto"); // Placeholder o errore
            System.err.println("WARNING: User not logged in when configuring navbar.");
        }
    }

    // Metodo di test, lascialo pure come lo hai, solo per debug
    int i = 0;
    @FXML
    public void cycleRole() {
        String[] usersTest = {"ADMIN PASSWORD", "volTest2 v2Test", "fruit1 pass1F"};

        Launcher.controller.interpreter("logout"); // Esegui logout prima
        Payload loginRes = Launcher.controller.interpreter("login " + usersTest[(i++)%3]); // Poi login

        if (loginRes != null && loginRes.getStatus() == Status.OK) {
             System.out.println("Role login successful: " + Launcher.controller.getCurrentUser().getUsername());
             showHome(); // Chiama showHome per riconfigurare la navbar con il nuovo ruolo
        } else {
             System.err.println("Cycled role login failed.");
             handleLogout(); // Torna al login se il test fallisce
        }
    }

    @FXML
    private void showHome() {
        // Configura la navbar prima di mostrare la home, basandosi sull'utente corrente
        Persona user = Launcher.controller.getCurrentUser();
        if (Launcher.controller != null && user != null) {
            configureNavbarForRole(user.getType());
        } else {
            // Questo caso non dovrebbe verificarsi se setupAfterLogin è chiamato solo post-login
            System.err.println("WARNING: showHome called without a logged-in user.");
            // Potresti voler disabilitare la navbar o mostrare uno stato di errore
            configureNavbarForRole(null); // Esempio: passa null o un tipo 'unknown'
        }


        contentArea.getChildren().clear();
        Parent homeFrame = contentFrames.get(HomeVisiteViewController.ID);
        if (homeFrame != null) {
             contentArea.getChildren().add(homeFrame);
             // new BounceIn(homeFrame).play(); // Se vuoi animazioni
        } else {
            System.err.println("Error: Home frame not found in contentFrames.");
            // Mostra un messaggio di errore nell'area contenuto
        }
    }

    // Metodi showX() per gli altri frame figli
    @FXML
    private void showDBluoghiEvisite() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(LuoghiVisiteViewController.ID);
         if (frame != null) {
            contentArea.getChildren().add(frame);
         } else {
             System.err.println("Error: LuoghiVisiteViewController frame not found in contentFrames.");
         }
    }

    @FXML
    private void showDBpersone() {
        contentArea.getChildren().clear();
         Parent frame = contentFrames.get(PersonViewController.ID);
         if (frame != null) {
            contentArea.getChildren().add(frame);
         } else {
             System.err.println("Error: PersonViewController frame not found in contentFrames.");
         }
    }

    @FXML
    private void showChangePassword() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(ChangePasswordViewController.ID);
         if (frame != null) {
            contentArea.getChildren().add(frame);
         } else {
             System.err.println("Error: ChangePasswordViewController frame not found in contentFrames.");
         }
    }

    @FXML
    private void showVolontario() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(VolontariViewController.ID);
         if (frame != null) {
            contentArea.getChildren().add(frame);
         } else {
             System.err.println("Error: VolontariViewController frame not found in contentFrames.");
         }
    }

    @FXML
    private void showFruitore() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(FruitoriViewController.ID);
         if (frame != null) {
            contentArea.getChildren().add(frame);
         } else {
             System.err.println("Error: FruitoriViewController frame not found in contentFrames.");
         }
    }

    @FXML
    private void handleLogout() {
        Payload res = Launcher.controller.interpreter("logout");

        if(res != null && res.getStatus() == Status.OK) {
             // Optional: clear contentArea before switching back to login
             contentArea.getChildren().clear();
             // Optional: reset navbar state if you want a clean slate on logout
             configureNavbarForRole(null); // Or some other placeholder state

            Launcher.setRoot(LoginViewController.ID);
        } else {
            // Handle logout failure if necessary
            System.err.println("Logout failed: " + (res != null ? res.getLogMessage() : "Unknown error"));
            // Optionally display an error message to the user
        }
    }

    public void toast(Payload<?> in) {
        Payload<String> p = (Payload<String>) in;
        Platform.runLater(() -> {
            String msg = p.getData();
            Payload.Level level = p.getLevel();
            Label toast = new Label(msg);
            toast.getStyleClass().addAll("toast", "toast-" + level.name().toLowerCase());
            toast.setFont(Font.font(14));
            toast.setPadding(new Insets(10));

            // Position in top-right of container
            toastContainer.getChildren().add(toast);

            // Slide in
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toast);
            slideIn.setFromX(-320);
            slideIn.setToX(-320); // move left into view
            slideIn.setOnFinished(e -> toast.setOpacity(1));

            // Pause visible
            PauseTransition pause = new PauseTransition(Duration.seconds(5));

            // Slide out
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), toast);
            slideOut.setFromX(-320);
            slideOut.setToX(0);
            slideOut.setOnFinished(e -> toastContainer.getChildren().remove(toast));

            SequentialTransition seq = new SequentialTransition(slideIn, pause, slideOut);
            seq.play();
        });
    }
}