package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AddPersonDialogController {

    @FXML
    private Label roleLabel;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    private PersonaType role; // Il ruolo da assegnare alla nuova persona
    private PersonViewController parentController; // Riferimento al controller principale

    // Setter per il ruolo
    public void setRole(PersonaType role) {
        this.role = role;
        roleLabel.setText("   AGGIUNGI  " + role + "    ");
    }

    // Setter per il controller principale
    public void setParentController(PersonViewController controller) {
        this.parentController = controller;
    }

    // Handler per il pulsante Conferma
    @FXML
    private void onConfirm() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            Launcher.getInstance().toast(Payload.error("Entrambi i campi sono obbligatori!",""));
            return;
        }

        String prompt = null;
        switch (role) {
            case CONFIGURATORE -> prompt = "add -c " + username + " " + password;
            case VOLONTARIO    -> prompt = "add -v " + username + " " + password;
            default -> {}
        }

        if(prompt == null)
            return;

        Payload<?> out = Launcher.getInstance().controller.interpreter(prompt);
        Launcher.getInstance().toast(out);

        switch (role) {
            case CONFIGURATORE -> parentController.refreshConfiguratori();
            case VOLONTARIO    -> parentController.refreshVolontari();
            default -> {}
        }

        closeDialog();
    }

    // Handler per il pulsante Annulla
    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        parentController.closeDialog();
    }
}
