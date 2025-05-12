package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddLuoghiDialogController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField titoloLuogo;
    @FXML
    private TextArea descrizioneLuogo;
    @FXML
    private TextArea posizione;

    private LuoghiViewController parentController;

    public void setParentController(LuoghiViewController controller) {
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
            System.out.println(titoloLuogo + descrizioneLuogo + posizione);
            errorLabel.setText("Tutti i campi sono obbligatori!");
            return;
        }

        Payload res = Launcher.controller.interpreter(String.format("add -L %s %s %s", titoloLuogo, descrizioneLuogo, posizione));

        if(res != null && res.getStatus() == Status.OK)
            parentController.addItem(Launcher.controller.getDB().dbLuoghiHelper.findLuogo(titoloLuogo));

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
