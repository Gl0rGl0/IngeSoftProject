package GUI.it.proj.frame;

import GUI.it.proj.utils.Luogo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddLuoghiDialogController {

    @FXML
    private Label errorLabel;
    @FXML
    private Label titleLabel;

    @FXML
    private TextField titoloLuogo;
    @FXML
    private TextArea descrizioneLuogo;
    @FXML
    private TextField lat;
    @FXML
    private TextField lon;

    @FXML
    private ComboBox<String> visitComboBox;

    private boolean edit = false;
    private LuoghiViewController parentController; // Riferimento al controller principale
    private LuoghiVisiteViewController superParentController;

    @FXML
    public void initialize() {
        titleLabel.setText("\t\tAGGIUNGI  LUOGO\t\t");

        ObservableList<String> visits = FXCollections.observableArrayList("Luogo 1", "Luogo 2", "Luogo 3");
        visitComboBox.setItems(visits);
    }

    public void setParentController(LuoghiViewController controller) {
        this.parentController = controller;
    }

    public void setSuperParentController(LuoghiVisiteViewController superParentController) {
        this.superParentController = superParentController;
    }

    // Handler per il pulsante Conferma
    @FXML
    private void onConfirm() {
        String titoloLuogo = this.titoloLuogo.getText();
        String descrizioneLuogo = this.descrizioneLuogo.getText();

        boolean areNum = true;
        int latitudine = -1;
        int longitudine = -1;
        try {
            latitudine = Integer.parseInt(lat.getText());
            longitudine = Integer.parseInt(lon.getText());
        } catch (NumberFormatException e) {
            areNum = false;
        }

        if (!areNum) {
            errorLabel.setText("Inserire latitudine e longitudine corretti!");
            return;
        }

        if (titoloLuogo == null || titoloLuogo.trim().isEmpty() ||
                descrizioneLuogo == null || descrizioneLuogo.trim().isEmpty() ||
                latitudine == -1 || longitudine == -1) {
            errorLabel.setText("Tutti i campi sono obbligatori!");
            return;
        }

        Luogo newLuogo = new Luogo(titoloLuogo, descrizioneLuogo, latitudine + ":" + longitudine);

        // Aggiungi la nuova Luogo alla ListView appropriata
        if (edit) {
            parentController.modifyItem(newLuogo);
        } else {
            parentController.addItem(newLuogo);
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
        titleLabel.setText("   MODIFICA  LUOGO   ");
    }

}
