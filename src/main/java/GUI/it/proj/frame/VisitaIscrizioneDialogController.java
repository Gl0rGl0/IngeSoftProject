package GUI.it.proj.frame;

import com.dlsc.unitfx.IntegerInputField;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.util.Payload;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller del dialog per creare/modificare una Visita.
 * Contiene i campi: titoloVisita, descrizioneVisita, posizione; 
 * e bottoni Confirm/Cancel.
 */
public class VisitaIscrizioneDialogController {

    @FXML private Label titleVisit;
    @FXML private Label descriptionVisit;
    @FXML private Label dateVisit;
    @FXML private Label positionVisit;
    @FXML private Label currentSub;
    @FXML private Label timeStart;
    @FXML private IntegerInputField numSub;

    @FXML private Button btnConferma;

    @FXML
    public void initialize(){
        numSub.setValue(1);
    }

    // Riferimento al controller padre (HomeVisiteViewController)
    private HomeVisiteViewController parentController;

    public void setParentController(HomeVisiteViewController controller) {
        this.parentController = controller;
    }

    /**
     * Se viene aperto il dialog con una Visita già esistente,
     * possiamo pre‐caricare i campi. Se invece passo null, 
     * consideriamo il form come "nuova Visita".
     */
    public void setVisitaCorrente(Visita v) {
        if(v == null) error();

        TipoVisita t = v.getTipoVisita();

        if(t == null) error();

        titleVisit.setText(t.getTitle());
        descriptionVisit.setText(t.getDescription());
        positionVisit.setText(t.getMeetingPlace());
        timeStart.setText(t.getInitTime().toString());

        dateVisit.setText(v.getDate().toString());
        currentSub.setText(v.getCurrentNumber()+"");
    }
    
    private void error() {
        Launcher.getInstance().toast(Payload.error("Cannot load visit now. Try again later.", null));
        closeDialog();
    }

    /**
     * Handler per il pulsante "Confirm".
     * Se visitaCorrente == null -> comando "add"
     * Altrimenti -> comando "update" (ipotizzando che l’interprete supporti 
     * l’update con id o simile).
     */
    @FXML
    private void onConfirm() {
        String title = titleVisit.getText();
        String date = dateVisit.getText();
        int quantity = numSub.getValue();

        Payload<?> res = Launcher.getInstance().controller.interpreter(String.format("visit -a \"%s\" %s %s", title, date, quantity));
        Launcher.getInstance().toast(res);

        // Se l’operazione è andata a buon fine (INFO), ricaricare la lista delle visite
        if (res != null && res.getStatus() == Payload.Status.INFO) {
            parentController.refreshItems();
            closeDialog();
        }
    }

    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        titleVisit.setText("");
        descriptionVisit.setText("");
        dateVisit.setText("");
        positionVisit.setText("");
        currentSub.setText("");
        timeStart.setText("");

        numSub.setValue(1);

        // Semplicemente chiama il metodo del parent
        if (parentController != null) {
            parentController.closeDialog();
        }
    }

    public void toggleConfirmBUtton(boolean b) {
        if(b != btnConferma.isDisabled()){
            numSub.setDisable(b);
            btnConferma.setDisable(b);
        }
    }
}
