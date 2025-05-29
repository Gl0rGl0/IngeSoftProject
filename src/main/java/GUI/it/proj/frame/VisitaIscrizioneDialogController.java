package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import V5.Ingsoft.util.Payload;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class VisitaIscrizioneDialogController {

    @FXML private TextField titoloLuogo;
    @FXML private TextArea descrizioneLuogo;
    @FXML private TextField posizione;

    private HomeVisiteViewController parentController;

    public void setParentController(HomeVisiteViewController controller) {
        this.parentController = controller;
    }

    // Handler per il pulsante Conferma
    @FXML
    private void onConfirm() {
        String titoloLuogo = this.titoloLuogo.getText();
        String descrizioneLuogo = this.descrizioneLuogo.getText();
        String posizione = this.posizione.getText();

        if (titoloLuogo == null      || titoloLuogo.isBlank() ||
            descrizioneLuogo == null || descrizioneLuogo.isBlank() ||
            posizione == null        || posizione.isBlank()) {
            Launcher.toast(Payload.error("Tutti i campi sono obbligatori!", ""));
            return;
        }

        Payload<?> res = Launcher.controller.interpreter(String.format("add -L %s %s %s", titoloLuogo, descrizioneLuogo, posizione));
        Launcher.toast(res);

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
