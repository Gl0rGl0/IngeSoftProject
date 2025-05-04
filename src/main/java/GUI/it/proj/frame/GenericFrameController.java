package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Loader;
import GUI.it.proj.utils.Persona;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GenericFrameController implements Initializable {
    public final static String ID = "generic";

    // Mappa degli FXML caricati (esempio per change password, ecc.)
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

    private String[] roles = { "CONFIGURATORE", "VOLONTARIO", "FRUITORE" };
    private ArrayList<Persona> db = new ArrayList<>();
    private Persona current;

    private ArrayList<Persona> genP(int n) {
        ArrayList<Persona> out = new ArrayList<>();
        for (int i = 0; i < n; i++)
            out.add(new Persona(i + "u", i + "p", roles[i % 3]));
        return out;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Allinea l'area di contenuto
        contentArea.setAlignment(Pos.CENTER);

        // Carica eventuali frame (ad esempio change password)
        contentFrames.put(ChangePasswordViewController.ID, Loader.loadFXML("changepsw-view"));
        contentFrames.put(PersonViewController.ID, Loader.loadFXML("persone-view"));
        contentFrames.put(LuoghiVisiteViewController.ID, Loader.loadFXML("luoghi-visite-view"));
        contentFrames.put(VolontariViewController.ID, Loader.loadFXML("volontari-view"));
        contentFrames.put(FruitoriViewController.ID, Loader.loadFXML("fruitori-view"));
        contentFrames.put(HomeVisiteViewController.ID, Loader.loadFXML("home-view"));

        // Supponiamo di aver già determinato il ruolo dall'autenticazione
        // Per esempio, inizialmente assumiamo CONFIGURATORE
        db = genP(3);
        current = db.getFirst();

        showHome();
    }

    /**
     * Configura la navbar in base al ruolo corrente.
     */
    private void configureNavbarForRole(String role) {
        // Nascondi tutti gli elementi specifici
        databaseButton.setVisible(false);
        databaseButton.setManaged(false);
        volontarioButton.setVisible(false);
        volontarioButton.setManaged(false);
        prenotazioniButton.setVisible(false);
        prenotazioniButton.setManaged(false);

        // Abilita gli elementi per il ruolo specifico
        switch (role) {
            case "CONFIGURATORE":
                databaseButton.setVisible(true);
                databaseButton.setManaged(true);
                break;
            case "VOLONTARIO":
                volontarioButton.setVisible(true);
                volontarioButton.setManaged(true);
                break;
            case "FRUITORE":
                prenotazioniButton.setVisible(true);
                prenotazioniButton.setManaged(true);
                break;
            default:
                // Nessun elemento specifico abilitato
                break;
        }

        int i = switch (current.tipo) {
            case "CONFIGURATORE" -> 1;
            case "FRUITORE" -> 3;
            case "VOLONTARIO" -> 2;
            default -> 0;
        };
        userMenuButton.setText(current.username + " " + i); // METTI IL NOME DEL ROLE
    }

    int i = 0;

    /**
     * Metodo di test per ciclare tra i ruoli (CONFIGURATORE, VOLONTARIO, FRUITORE).
     * Questo metodo è solo a scopo dimostrativo.
     */
    @FXML
    private void cycleRole() {
        current = db.get((i++) % db.size());
        configureNavbarForRole(current.tipo);
    }

    @FXML
    private void showHome() {
        configureNavbarForRole(current.tipo);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(contentFrames.get(HomeVisiteViewController.ID));
        // Label test = new Label("Home");
        // contentArea.getChildren().add(test);

        // new BounceIn(test).play();

        // showDBluoghiEvisite();
        // showDBpersone();
        // showVolontario();
    }

    @FXML
    private void showDBluoghiEvisite() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(contentFrames.get(LuoghiVisiteViewController.ID));
    }

    @FXML
    private void showDBpersone() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(contentFrames.get(PersonViewController.ID));
    }

    @FXML
    private void showChangePassword() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(contentFrames.get(ChangePasswordViewController.ID));
    }

    @FXML
    private void showVolontario() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(contentFrames.get(VolontariViewController.ID));
    }

    @FXML
    private void showFruitore() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(contentFrames.get(FruitoriViewController.ID));
    }

    @FXML
    private void handleLogout() {
        Launcher.setRoot(LoginViewController.ID);
    }
}