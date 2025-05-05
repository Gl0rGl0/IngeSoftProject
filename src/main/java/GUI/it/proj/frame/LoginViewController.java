package GUI.it.proj.frame;

import java.net.URL;
import java.util.ResourceBundle;

import GUI.it.proj.Launcher;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController implements Initializable {
    public final static String ID = "login";

    @FXML
    private TextField userTextField;
    @FXML
    private PasswordField pwField;
    @FXML
    private Button loginButton;
    @FXML
    private Label messageLabel;

    /**
     * Metodo chiamato automaticamente dopo l'iniezione degli elementi FXML.
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        // Richiedi il focus sul bottone per evitare che un TextField venga selezionato
        Platform.runLater(() -> loginButton.requestFocus());
        messageLabel.setVisible(false);
        // test.setWrapText(true);
    }

    /**
     * Gestisce il click sul bottone di login.
     */
    @FXML
    private void handleLoginAction() {
        String username = userTextField.getText();
        String password = pwField.getText();
        System.out.println(username + " " + password);

        if (username == null || password == null || username.isEmpty() || password.isEmpty())
            return;

        Payload res = Launcher.controller.interpreter(String.format("login %s %s", username, password));
        System.out.println(res);
        if (res != null && res.getStatus() == Status.OK) {
            userTextField.clear();
            pwField.clear();
            Launcher.setRoot(GenericFrameController.ID);
        } else {
            messageLabel.setVisible(true);
        }
    }
}