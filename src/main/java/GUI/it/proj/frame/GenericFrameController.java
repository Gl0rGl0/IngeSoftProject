package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.io.IOException;
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
    @FXML private MenuButton userMenuButton;
    @FXML private StackPane contentArea;

    // Elementi specifici per ruolo
    @FXML private MenuButton databaseButton; // CONFIGURATORE
    @FXML private Button orarioManagerButton;

    @FXML private Button volontarioButton; // VOLONTARIO

    @FXML private Button prenotazioniButton; // FRUITORE

    @FXML private AnchorPane toastContainer;  // Overlay for toasts

    // Questa variabile conterrà il riferimento a questa istanza del controller,
    // utile per accedere a questo controller da metodi statici di Launcher.
    // MA con il nuovo approccio in Launcher.setRoot non è strettamente necessario.
    // private static GenericFrameController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentArea.setAlignment(Pos.CENTER);
    
        // Caricamenti FXML esistenti…
        loadBaseFrames();

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
            case CONFIGURATORE -> loadConfigFrame();
            case VOLONTARIO -> loadVolontFrame();
            case FRUITORE -> loadFruitFrame();
            default -> {}
        }
        
        showHome(); // Chiama showHome() che a sua volta configurerà la navbar
        
        if(Launcher.controller.getCurrentUser().isNew()){
            Launcher.toast(Payload.debug("Please change the password, this page will show up everytime until you change it.", ID));
            showChangePassword();
        }
    }

    HomeVisiteViewController homeController;
    ChangePasswordViewController changeController;
    private void loadBaseFrames(){

        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/home-view.fxml"));
            contentFrames.put(HomeVisiteViewController.ID, loader.load());
            homeController = loader.getController();

            loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/changepsw-view.fxml"));
            contentFrames.put(ChangePasswordViewController.ID, loader.load());
            changeController = loader.getController();
            changeController.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    LuoghiTipoVisiteViewController lEVcontroller;
    PersonViewController persController;
    OrarioViewController oController;
    private void loadConfigFrame(){
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/luoghi-tipovisite-view.fxml"));
            contentFrames.put(LuoghiTipoVisiteViewController.ID, loader.load());
            lEVcontroller = loader.getController();

            loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/persone-view.fxml"));
            contentFrames.put(PersonViewController.ID, loader.load());
            persController = loader.getController();

            loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/orario-view.fxml"));
            contentFrames.put(OrarioViewController.ID, loader.load());
            oController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FruitoriViewController fruitController;
    private void loadFruitFrame(){
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/fruitori-view.fxml"));
            contentFrames.put(FruitoriViewController.ID, loader.load());
            fruitController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VolontariViewController volontController;
    private void loadVolontFrame(){
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/volontari-view.fxml"));
            contentFrames.put(FruitoriViewController.ID, loader.load());
            volontController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura la navbar in base al ruolo corrente.
     */
    private void configureNavbarForRole(PersonaType role) {
        if(role == null)
            return;
        
        System.out.println("Configuring navbar for role: " + role);
        // Nascondi tutti gli elementi specifici
        databaseButton.setVisible(false);
        databaseButton.setManaged(false);
        orarioManagerButton.setVisible(false);
        orarioManagerButton.setManaged(false);
        
        volontarioButton.setVisible(false);
        volontarioButton.setManaged(false);
        
        prenotazioniButton.setVisible(false);
        prenotazioniButton.setManaged(false);

        // Abilita gli elementi per il ruolo specifico
        switch (role) {
            case CONFIGURATORE:
                databaseButton.setVisible(true);
                databaseButton.setManaged(true);

                orarioManagerButton.setVisible(true);
                orarioManagerButton.setManaged(true);
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
        String[] usersTest = {"ADMIN PASSWORD", "volTest2 v2Test", "fruit2 pass2F"};

        Payload<?> res = Launcher.controller.interpreter("logout");

        if(res != null && res.getStatus() != Status.ERROR) {
            contentArea.getChildren().clear();
            configureNavbarForRole(null);
            
            Launcher.toast(res);
            //Launcher.setRoot(LoginViewController.ID);
        } else {
            Launcher.toast(Payload.error("Logout failed", res.getLogMessage()));
            showHome();
            return;
        }

        Payload<?> loginRes = Launcher.controller.interpreter("login " + usersTest[(i++)%3]); // Poi login

        if (loginRes != null && loginRes.getStatus() != Status.ERROR) {
            Launcher.toast(Payload.info("Role login successful: " + Launcher.controller.getCurrentUser().getUsername(), ""));
            setupAfterLogin();
        } else {
            System.err.println("Cycled role login failed." + loginRes);
        }
    }

    @FXML void showHome() {
        // Configura la navbar prima di mostrare la home, basandosi sull'utente corrente
        Persona user = Launcher.controller.getCurrentUser();
        if (Launcher.controller != null && user != null) {
            configureNavbarForRole(user.getType());
        } else {
            // Questo caso non dovrebbe verificarsi se setupAfterLogin è chiamato solo post-login
            System.err.println("WARNING: showHome called without a logged-in user.");
            configureNavbarForRole(null); // Esempio: passa null o un tipo 'unknown'
        }


        contentArea.getChildren().clear();
        Parent homeFrame = contentFrames.get(HomeVisiteViewController.ID);
        if (homeFrame != null) {
                contentArea.getChildren().add(homeFrame);
                homeController.refreshItems();
        } else {
            System.err.println("Error: Home frame not found in contentFrames.");
        }
    }

    // Metodi showX() per gli altri frame figli
    @FXML
    private void showDBluoghiEvisite() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(LuoghiTipoVisiteViewController.ID);
        if (frame != null) {
            contentArea.getChildren().add(frame);
        } else {
            System.err.println("Error: LuoghiTipoVisiteViewController frame not found in contentFrames.");
        }
        lEVcontroller.refreshItems();
        persController.closeDialog();
    }

    @FXML
    private void showDBpersone() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(PersonViewController.ID);
        if (frame != null) {
            contentArea.getChildren().add(frame);
            persController.refreshItems();
        } else {
            System.err.println("Error: PersonViewController frame not found in contentFrames.");
        }
    }

    @FXML
    private void showOrarioManager() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(OrarioViewController.ID);
        if (frame != null) {
            contentArea.getChildren().add(frame);
            oController.refreshData();
        } else {
            System.err.println("Error: OrarioViewController frame not found in contentFrames.");
        }
    }

    @FXML
    private void showChangePassword() {
        contentArea.getChildren().clear();
        Parent frame = contentFrames.get(ChangePasswordViewController.ID);
        if (frame != null) {
            changeController.initialize();
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
            volontController.refreshItems();
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
            fruitController.refreshItems();
        } else {
            System.err.println("Error: FruitoriViewController frame not found in contentFrames.");
        }
    }

    @FXML
    private void handleLogout() {
        Payload<?> res = Launcher.controller.interpreter("logout");

        if(res != null && res.getStatus() == Status.INFO) {
            contentArea.getChildren().clear();
            configureNavbarForRole(null); // Or some other placeholder state
            
            Launcher.toast(res);
            Launcher.setRoot(LoginViewController.ID);
        } else {
            Launcher.toast(Payload.error("Logout failed", res.getLogMessage()));
            showHome();
        }
    }

    public void toast(Payload<?> in) {
        Payload<String> p = (Payload<String>) in;
        Platform.runLater(() -> {
            String msg = p.getData();
            Payload.Status status = p.getStatus();
            Label toast = new Label(msg);
            toast.getStyleClass().addAll("toast", "toast-" + status.name().toLowerCase());
            toast.setFont(Font.font(14));
            toast.setPadding(new Insets(10));
    
            // Aggiungi al container e ancora in alto a destra
            toastContainer.getChildren().add(toast);
            AnchorPane.setTopAnchor(toast, 10.0);
            AnchorPane.setRightAnchor(toast, 10.0);
    
            // Forza il layout per ottenere la larghezza reale
            toast.applyCss();
            toast.layout();
            double offset = toast.getWidth() + 20;
    
            // Slide in da destra
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toast);
            slideIn.setFromX(offset);
            slideIn.setToX(0);
    
            // Pausa visibile
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
    
            // Slide out verso destra
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), toast);
            slideOut.setFromX(0);
            slideOut.setToX(offset);
            slideOut.setOnFinished(e -> toastContainer.getChildren().remove(toast));
    
            new SequentialTransition(slideIn, pause, slideOut).play();
        });
    }    
}