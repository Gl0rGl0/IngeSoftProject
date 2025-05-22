package GUI.it.proj.frame;

import java.time.YearMonth;
import java.util.List;

import com.dlsc.unitfx.IntegerInputField;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OrarioViewController {
    public static final String ID = "orario";

    private boolean actual_status_isOpen = Launcher.controller.isVolunteerCollectionOpen();

    @FXML VBox container;
    @FXML Label status;
    @FXML Label stats;
    @FXML TextField ambitoField;
    @FXML IntegerInputField numMaxField;

    @FXML private void initialize() {
        initAvailability();

        if(actual_status_isOpen){
            status.getStyleClass().remove("error-label");
            status.getStyleClass().add("success-label");
            status.setText("APERTO");
        }else{
            status.getStyleClass().remove("success-label");
            status.getStyleClass().add("error-label");
            status.setText("CHIUSO");
        }
        
        numMaxField.setValue(getNumMax());
        ambitoField.setText(getAmbito());
    }

    private void initAvailability(){
        YearMonth yM = YearMonth.now().plusMonths(1);
        int daysInMonth = yM.lengthOfMonth();
    
        // Recupera la lista dei volontari
        List<Volontario> lista = Launcher
            .controller
            .getDB()
            .dbVolontarioHelper
            .getPersonList();
        lista.removeIf(v -> !v.isUsable());
    
        for (Volontario v : lista) {
            HBox row = new HBox();
            row.getStyleClass().add("vol-row");
            row.setSpacing(5);
    
            // Colonna dei nomi
            Label name = new Label(v.getUsername());
            name.setPrefWidth(140);
            row.getChildren().add(name);
    
            // Array di 31 booleani (true=disp, false=non disp)
            boolean[] disponibilita = v.getAvailability();
    
            // Crea un box per ciascun giorno effettivo del mese
            for (int day = 1; day <= daysInMonth; day++) {
                Label box = new Label();
                box.getStyleClass().add("day-box");
                boolean ok = disponibilita[day - 1];
                box.getStyleClass().add(ok ? "day-available" : "day-unavailable");
                
                // queste due righe in più
                HBox.setHgrow(box, Priority.ALWAYS);
                box.setMaxWidth(Double.MAX_VALUE);
            
                row.getChildren().add(box);
            }
    
            container.getChildren().add(row);
        }
    }

    public void refreshData(){
        
        YearMonth yM = YearMonth.now().plusMonths(1);
        int daysInMonth = yM.lengthOfMonth();
        
        List<Volontario> lista = Launcher
            .controller
            .getDB()
            .dbVolontarioHelper
            .getPersonList();
            lista.removeIf(v -> !v.isUsable());
            
            // assumiamo che initAvailability abbia aggiunto una riga per ogni volontario
            for(int i = 0; i < lista.size(); i++){
                Volontario v = lista.get(i);
                HBox row = (HBox) container.getChildren().get(i);
                boolean[] dispon = v.getAvailability();
                
                // il primo figlio di row è il Label con il nome, quindi partiamo da 1
                for(int day = 1; day <= daysInMonth; day++){
                    Label box = (Label) row.getChildren().get(day);
                    // rimuovo la classe precedente e ne applico una nuova
                    box.getStyleClass().removeAll("day-available","day-unavailable");
                    box.getStyleClass().add(dispon[day-1]
                    ? "day-available"
                    : "day-unavailable");
                }
            }

        refreshStats(daysInMonth);
    }

    @FXML private void onOpenCollection() {
        if(actual_status_isOpen)
            return;

        Payload<?> res = Launcher.controller.interpreter("collection -o");

        if(res == null || res.getStatus() != Status.INFO){
            Launcher.toast(res);
            return;
        }

        status.getStyleClass().remove("error-label");
        status.getStyleClass().add("success-label");
        status.setText("APERTO");
    }

    @FXML private void onCloseCollection(){
        if(!actual_status_isOpen)
            return;

        Payload<?> res = Launcher.controller.interpreter("collection -c");

        if(res == null || res.getStatus() != Status.INFO){
            Launcher.toast(res);
            return;
        }

        status.getStyleClass().remove("success-label");
        status.getStyleClass().add("error-label");
        status.setText("CHIUSO");
    }
    
    @FXML private void onMakeOrario() {
        Payload<?> res = Launcher.controller.interpreter("makeplan");
        Launcher.toast(res);
    }

    private void refreshStats(int lenght){
        double res = Launcher.controller.getVolontariStats() / lenght;
        stats.setText(String.format("%.2f%%", res * 100));
    }

    @FXML private void onConfirmNum(){
        if(numMaxField.getValue() == null)
            return;
        
        int use = numMaxField.getValue();

        if(use <= 0)
            return;

        Payload<?> res = Launcher.controller.interpreter("setmax " + use);
        Launcher.toast(res);
    }

    private String getAmbito(){
        return Model.appSettings.getAmbitoTerritoriale();
    }

    private int getNumMax(){
        return Model.appSettings.getMaxPrenotazioniPerPersona();
    }
}