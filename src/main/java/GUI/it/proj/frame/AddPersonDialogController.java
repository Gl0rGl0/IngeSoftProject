package GUI.it.proj.frame;

import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AddPersonDialogController {

    @FXML
    private Label errorLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox<String> visitComboBox;

    private boolean edit = false;
    private PersonaType role; // Il ruolo da assegnare alla nuova persona
    private PersonViewController parentController; // Riferimento al controller principale

    // Setter per il ruolo
    public void setRole(PersonaType role) {
        this.role = role;
        roleLabel.setText("   AGGIUNGI  " + role + "    ");

        // Se il ruolo è "Volontario", rendi visibile il ComboBox
        if (role.equals(PersonaType.VOLONTARIO)) {
            ObservableList<String> visits = FXCollections.observableArrayList("Visita 1", "Visita 2", "Visita 3");
            visitComboBox.setItems(visits);

            visitComboBox.setVisible(true);
            visitComboBox.setManaged(true);
        } else {
            visitComboBox.setVisible(false);
        }
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
            errorLabel.setText("Entrambi i campi sono obbligatori!");
            System.out.println("Inserisci sia username che password");
            return;
        }

        if (edit) {
            String selectedVisit = visitComboBox.getValue();
            if (selectedVisit != null) {
                System.out.println("Volontario associato alla visita: " + selectedVisit);
            }
        } else {
            Persona newPersona;
            try{
                newPersona = switch(role){
                    case CONFIGURATORE -> new Configuratore(username, password, true, true);
                    case VOLONTARIO -> new Volontario(username, password, true, true);
                    default -> throw new Exception();
                };
            } catch(Exception e) {
                closeDialog();
                return;
            }

            parentController.addItem(newPersona);
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

    public void setStatus(boolean editMode, String username) {
        if (!editMode)
            return;

        edit = true;
        txtUsername.setText(username);
        txtPassword.setText("*****");
        txtPassword.setDisable(true);
        txtUsername.setDisable(true);
        roleLabel.setText("   MODIFICA  " + role + "    ");
    }

}
