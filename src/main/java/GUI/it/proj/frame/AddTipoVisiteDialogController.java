package GUI.it.proj.frame;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.dlsc.gemsfx.TimePicker;
import com.dlsc.unitfx.IntegerInputField;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.persone.Volontario;
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
    @FXML private CheckComboBox<Volontario> volontariUIDs;

    @FXML private ComboBox<String> placeComboBox;

    private boolean edit = false;
    private TipoVisiteViewController parentController;

    @FXML
    public void initialize() {
        giorni.getItems().addAll("Lu", "Ma", "Me", "Gi", "Ve", "Sa", "Do");
        ObservableList<String> places = FXCollections.observableArrayList("Visita 1", "Visita 2", "Visita 3");
        placeComboBox.setItems(places);

        ObservableList<Volontario> voloList = FXCollections.observableArrayList(Launcher.controller.getDB().dbVolontarioHelper.getPersonList());
        volontariUIDs.getItems().addAll(voloList);
    }

    // Setter per il controller principale
    public void setParentController(TipoVisiteViewController controller) {
        this.parentController = controller;
    }

    // Handler per il pulsante Conferma
    @FXML
    private void onConfirm() {
        // 1. Lettura testi
        String titolo = titoloVisita.getText();
        String descr  = descrizioneVisita.getText();
        String luogo  = posizione.getText();
        
        // 2. Lettura date e durata
        LocalTime ora    = oraInizio.getTime();
        LocalDate inizio = inizioGiornoPeriodo.getValue();
        LocalDate fine   = fineGiornoPeriodo.getValue();
        Integer dur      = durata.getValue();
        Integer minPart  = numeroMinimoPartecipanti.getValue();
        Integer maxPart  = numeroMassimoPartecipanti.getValue();

        // 3. Selezioni multichoice
        List<String> giorniSel = giorni.getCheckModel().getCheckedItems();

        List<Volontario> volsSel   = volontariUIDs
            .getCheckModel()
            .getCheckedItems();

        List<Volontario> volNotSel = new ArrayList<>(volontariUIDs.getItems());
        volNotSel.removeAll(volsSel);

        // 4. Validazione di tutti i campi
        if (titolo    == null || titolo.isBlank() ||
            descr     == null || descr.isBlank() ||
            luogo     == null || luogo.isBlank() ||
            inizio    == null ||
            fine      == null ||
            ora       == null ||
            dur       == null ||
            minPart   == null ||
            maxPart   == null ||
            giorniSel.isEmpty() ||
            volsSel.isEmpty()) {          
            
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
        
        TipoVisita nuova = Launcher.controller.getDB().dbTipoVisiteHelper.findTipoVisita(titolo);
        if(nuova == null) return;
        
        for(Volontario s : volsSel){
            if(nuova.assignedTo(s.getUsername())) continue;
            
            Launcher.controller.interpreter(String.format("assign -V %s %s", titolo, s));
        }

        for(Volontario s : volNotSel){
            if(!nuova.assignedTo(s.getUsername())) continue;
            
            Launcher.controller.interpreter(String.format("disassign -V %s %s", titolo, s));
        }
        
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

    public void setEdit(boolean edit) {
        this.edit = true;
        if (edit) {
            titleLabel.setText("   MODIFICA  VISITA   ");
        } else {
            titleLabel.setText("   AGGIUNGO  VISITA   ");
        }
    }

}

//TODO EDIT