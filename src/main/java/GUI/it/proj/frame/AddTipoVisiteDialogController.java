package GUI.it.proj.frame;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.dlsc.gemsfx.TimePicker;
import com.dlsc.unitfx.IntegerInputField;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddTipoVisiteDialogController {

    @FXML
    private Label titleLabel;

    @FXML private TextField titoloVisita;
    @FXML private TextArea descrizioneVisita;
    @FXML private TextField posizione;
    @FXML private DatePicker inizioGiornoPeriodo;
    @FXML private DatePicker fineGiornoPeriodo;
    @FXML private TimePicker oraInizio;
    @FXML private IntegerInputField durata;
    @FXML private CheckBox gratuito;
    @FXML private IntegerInputField numeroMinimoPartecipanti;
    @FXML private IntegerInputField numeroMassimoPartecipanti;
    @FXML private CheckComboBox<String> giorni;
    @FXML private CheckComboBox<Volontario> volontariCheckBox;

    @FXML private ComboBox<Luogo> placeComboBox;

    private boolean edit = false;
    private TipoVisiteViewController parentController;

    @FXML
    public void initialize() {
        giorni.getItems().addAll("Lu", "Ma", "Me", "Gi", "Ve", "Sa", "Do");

        refreshPlaceList();
        refreshVolList();

        clearChecks();
    }
    
    private void refreshPlaceList() {
        List<Luogo> use = Model.getInstance().dbLuoghiHelper.getItems();
        use.removeIf(l -> !l.isUsable());

        placeComboBox.getItems().clear();
        ObservableList<Luogo> places = FXCollections.observableArrayList(use);
        placeComboBox.getItems().addAll(places);
        
    }

    private void refreshVolList(){
        List<Volontario> use = Model.getInstance().dbVolontarioHelper.getItems();
        use.removeIf(v -> !v.isUsable());
        
        volontariCheckBox.getItems().clear();
        ObservableList<Volontario> voloList = FXCollections.observableArrayList(use);
        volontariCheckBox.getItems().addAll(voloList);
    }
    
    public void clearChecks(){
        giorni.getCheckModel().clearChecks();
        placeComboBox.getSelectionModel().clearSelection();
        volontariCheckBox.getCheckModel().clearChecks();
    }

    // Setter per il controller principale
    public void setParentController(TipoVisiteViewController controller) {
        this.parentController = controller;
    }

    // Handler per il pulsante Conferma
    @FXML
    private void onConfirm() {
        if(edit){
            onEdit();
            return;
        }
        // 1. Lettura testi
        String titolo    = titoloVisita.getText();
        String descr     = descrizioneVisita.getText();
        String pos       = posizione.getText();
        
        // 2. Lettura date e durata
        LocalTime ora    = oraInizio.getTime();
        LocalDate inizio = inizioGiornoPeriodo.getValue();
        LocalDate fine   = fineGiornoPeriodo.getValue();
        Integer dur      = durata.getValue();
        Integer minPart  = numeroMinimoPartecipanti.getValue();
        Integer maxPart  = numeroMassimoPartecipanti.getValue();

        // 3. Selezioni multichoice
        List<String> giorniSel = giorni.getCheckModel().getCheckedItems();

        List<Volontario> volsSel   = volontariCheckBox
            .getCheckModel()
            .getCheckedItems();

        List<Volontario> volNotSel = new ArrayList<>(volontariCheckBox.getItems());
        volNotSel.removeAll(volsSel);

        Luogo luogo = placeComboBox.getSelectionModel().getSelectedItem();

        // 4. Validazione di tutti i campi
        if (titolo    == null || titolo.isBlank() ||
            descr     == null || descr.isBlank() ||
            pos     == null   || pos.isBlank() ||
            inizio    == null ||
            fine      == null ||
            ora       == null ||
            dur       == null ||
            minPart   == null ||
            maxPart   == null ||
            giorniSel.isEmpty() ||
            volsSel.isEmpty() ||
            luogo == null) {          
            
            Launcher.toast(Payload.error("Tutti i campi sono obbligatori!",""));
            return;
        }

        // 5. Costruzione CSV per giorni e volontari
        String giorniCsv = String.join("", giorniSel);
        
        // 6. Preparazione stringa di comando
        //   Virgolette per i campi che potrebbero contenere spazi
        String cmd = String.format(
            "\"%s\" \"%s\" \"%s\" %d/%d/%d %d/%d/%d %d:%d %d %b %d %d %s",
            titolo,
            descr,
            luogo,
            inizio.getDayOfMonth(), inizio.getMonthValue(), inizio.getYear(),
            fine.getDayOfMonth(), fine.getMonthValue(), fine.getYear(),
            ora.getMinute(), ora.getHour(),
            dur,
            gratuito.isSelected(),
            minPart,
            maxPart,
            giorniCsv );

        Payload<?> res = Launcher.controller.interpreter("add -T " + cmd);
        Launcher.toast(res);

        // 8. Gestione risultato
        if (res == null || res.getStatus() == Status.ERROR) return;
        
        manageAssign();

        res = Launcher.controller.interpreter(String.format("assign -L \"%s\" %s", luogo.getName(), titolo));
        Launcher.toast(res);

        parentController.refreshItems();
        closeDialog();
    }

    private void manageAssign(){
        String titolo    = titoloVisita.getText();
        TipoVisita visit = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(titolo);
        if(visit == null) return;

        List<Volontario> volsSel   = volontariCheckBox
            .getCheckModel()
            .getCheckedItems();

        List<Volontario> volNotSel = new ArrayList<>(volontariCheckBox.getItems());
        volNotSel.removeAll(volsSel);

        for(Volontario s : volsSel){
            if(visit.assignedTo(s.getUsername())) continue;
            
            Launcher.controller.interpreter(String.format("assign -V \"%s\" %s", visit.getTitle(), s.getUsername()));
        }

        for(Volontario s : volNotSel){
            if(!visit.assignedTo(s.getUsername())) continue;
            
            Launcher.controller.interpreter(String.format("disassign -V \"%s\" %s", visit.getTitle(), s.getUsername()));
        }
    }
        
        
    private void onEdit() {
        if(volontariCheckBox.getCheckModel().getCheckedItems().isEmpty()){
            Launcher.toast(Payload.error("Cannot save without any volunteer assigned! Please assign at least one.", ""));
            return;
        }
        
        manageAssign();
        parentController.refreshItems();
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

    public void setEdit(boolean e, TipoVisita v) {
        if (e) {
            edit = e;
            titleLabel.setText("   EDIT  VISIT   ");

            disableThings(true);

            titoloVisita.setText(v.getTitle());
            descrizioneVisita.setText(v.getDescription());
            posizione.setText(v.getMeetingPlace());
            inizioGiornoPeriodo.setValue(v.getInitDay().localDate.toLocalDate());
            fineGiornoPeriodo.setValue(v.getFinishDay().localDate.toLocalDate());
            oraInizio.setValue(LocalTime.parse(v.getInitTime().toString()));
            durata.setValue(v.getDuration());
            gratuito.selectedProperty().set(v.isFree());
            numeroMinimoPartecipanti.setValue(v.getNumMinPartecipants());
            numeroMassimoPartecipanti.setValue(v.getNumMaxPartecipants());

            Luogo l = Model.getInstance().dbLuoghiHelper.getItem(v.getLuogoUID());
            //nullity gestita internamente dal model
            placeComboBox.getSelectionModel().select(l);

            for (String uid : v.getVolontariUIDs()) {
                for (Volontario vol : volontariCheckBox.getItems()) {
                    if (vol.getUsername().equals(uid)) {
                        volontariCheckBox.getCheckModel().check(vol);
                    }
                }
            }

        } else {
            titleLabel.setText("   ADD  VISIT   ");
            disableThings(false);
            
            titoloVisita.clear();
            descrizioneVisita.clear();
            posizione.clear();
            inizioGiornoPeriodo.setValue(LocalDate.now());
            fineGiornoPeriodo.setValue(LocalDate.now());
            oraInizio.setValue(LocalTime.now());
            durata.clear();
            gratuito.selectedProperty().set(false);
            numeroMinimoPartecipanti.clear();
            numeroMassimoPartecipanti.clear();
        }
    }

    private void disableThings(boolean b){
        titoloVisita.setDisable(b);
        descrizioneVisita.setDisable(b);
        posizione.setDisable(b);
        inizioGiornoPeriodo.setDisable(b);
        fineGiornoPeriodo.setDisable(b);
        oraInizio.setDisable(b);
        durata.setDisable(b);
        gratuito.setDisable(b);
        numeroMinimoPartecipanti.setDisable(b);
        numeroMassimoPartecipanti.setDisable(b);
        giorni.setDisable(true);
        placeComboBox.setDisable(b);
    }

}