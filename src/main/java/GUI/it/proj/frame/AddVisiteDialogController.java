package GUI.it.proj.frame;

import GUI.it.proj.utils.Visita;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddVisiteDialogController {

    @FXML
    private Label errorLabel;
    @FXML
    private Label titleLabel;

    @FXML
    private TextField titoloVisita;
    @FXML
    private TextField descrizioneVisita;
    @FXML
    private TextField lat;
    @FXML
    private TextField lon;
    @FXML
    private TextField oraInizio;
    @FXML
    private TextField numeroMinimoPartecipanti;
    @FXML
    private TextField numeroMassimoPartecipanti;

    @FXML
    private ComboBox<String> visitComboBox;

    private boolean edit = false;
    private VisiteViewController parentController; // Riferimento al controller principale
    private LuoghiVisiteViewController superParentController;

    @FXML
    public void initialize() {
        titleLabel.setText("   AGGIUNGI  VISITA   ");

        ObservableList<String> visits = FXCollections.observableArrayList("Visita 1", "Visita 2", "Visita 3");
        visitComboBox.setItems(visits);
    }

    // Setter per il controller principale
    public void setParentController(VisiteViewController controller) {
        this.parentController = controller;
    }

    public void setSuperParentController(LuoghiVisiteViewController superParentController) {
        this.superParentController = superParentController;
    }

    // Handler per il pulsante Conferma
    @FXML
    private void onConfirm() {
        String titoloVisita = this.titoloVisita.getText();
        String descrizioneVisita = this.descrizioneVisita.getText();

        if (titoloVisita == null || titoloVisita.trim().isEmpty() ||
                descrizioneVisita == null || descrizioneVisita.trim().isEmpty()) {
            errorLabel.setText("Entrambi i campi sono obbligatori!");
            System.out.println("Inserisci sia username che password");
            return;
        }

        // Crea la nuova Luogo (qui potresti impostare anche una password di default
        // se necessario)
        Visita newVisita = new Visita(titoloVisita, descrizioneVisita);

        // Aggiungi la nuova Luogo alla ListView appropriata
        if (edit) {
            parentController.modifyItem(newVisita);
        } else {
            parentController.addItem(newVisita);
        }
        // Gestisci l'associazione alla visita
        String selectedVisit = visitComboBox.getValue();
        if (selectedVisit != null) {
            System.out.println("Volontario associato alla visita: " + selectedVisit);
        }
        // Chiudi il dialogo
        closeDialog();
    }

    // Handler per il pulsante Annulla
    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        superParentController.closeDialog();
    }

    public void setEdit(boolean edit) {
        this.edit = true;
        if (edit) {
            titleLabel.setText("   MODIFICA  VISITA   ");
        } else {

            titleLabel.setText("   AGGIUNGO  VISITA   ");
        }
    }

}
