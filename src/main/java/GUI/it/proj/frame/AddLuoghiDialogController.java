package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import V5.Ingsoft.util.Payload;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddLuoghiDialogController {

    @FXML private TextField titoloLuogo;
    @FXML private TextArea descrizioneLuogo;
    @FXML private TextField posizione;

    private LuoghiViewController parentController;

    public void setParentController(LuoghiViewController controller) {
        this.parentController = controller;
    }

    // Handler per il pulsante Confirm
    @FXML
    private void onConfirm() {
        String titoloLuogo = this.titoloLuogo.getText();
        String descrizioneLuogo = this.descrizioneLuogo.getText();
        String posizione = this.posizione.getText();

        if (titoloLuogo == null      || titoloLuogo.isBlank() ||
            descrizioneLuogo == null || descrizioneLuogo.isBlank() ||
            posizione == null        || posizione.isBlank()) {
            Launcher.getInstance().toast(Payload.error("Tutti i campi sono obbligatori!", ""));
            return;
        }

        Payload<?> res = Launcher.getInstance().controller.interpreter(String.format("add -L %s %s %s", titoloLuogo, descrizioneLuogo, posizione));
        Launcher.getInstance().toast(res);

        closeDialog();
    }

    // Handler per il pulsante Cancel
    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        parentController.closeDialog();
    }

}
