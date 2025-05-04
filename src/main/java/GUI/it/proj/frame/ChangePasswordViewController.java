package GUI.it.proj.frame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import java.net.URL;
import java.util.ResourceBundle;
import GUI.it.proj.Launcher;

public class ChangePasswordViewController implements Initializable {
    public final static String ID = "changepsw";

    // Se i nodi FXML sono definiti nel file FXML, dichiarali con @FXML
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button resetButton;
    @FXML
    private Label messageLabel;

    private String phase; // "change" o "home"

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        phase = "change";

        resetButton.requestFocus();
    }

    @FXML
    public void handleResetAction() {
        if (phase.equals("change")) {
            String newPass = passwordField.getText();
            String confirmPass = confirmPasswordField.getText();

            if (newPass.length() < 6) {
                messageLabel.setText("La password deve avere almeno 6 caratteri");
                messageLabel.getStyleClass().removeAll("success-label");
                if (!messageLabel.getStyleClass().contains("error-label"))
                    messageLabel.getStyleClass().add("error-label");
                messageLabel.setVisible(true);
            } else {
                if (newPass.equals(confirmPass)) {
                    // LOGICA CAMBIO PASSWORD
                    messageLabel.setText("Password cambiata con successo");
                    messageLabel.getStyleClass().remove("error-label");
                    if (!messageLabel.getStyleClass().contains("success-label"))
                        messageLabel.getStyleClass().add("success-label");

                    resetButton.getStyleClass().add("btn-success");

                    messageLabel.setVisible(true);
                    resetButton.setText("Continua");
                    phase = "home";
                } else {
                    messageLabel.setText("Le password non coincidono");
                    messageLabel.getStyleClass().removeAll("success-label");
                    if (!messageLabel.getStyleClass().contains("error-label"))
                        messageLabel.getStyleClass().add("error-label");
                    messageLabel.setVisible(true);
                }
            }
        } else {
            Launcher.setRoot(GenericFrameController.ID, true);
        }
    }
}