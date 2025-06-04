package GUI.it.proj.frame;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.dlsc.unitfx.IntegerInputField;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OrarioViewController {
    public static final String ID = "orario";

    private boolean actual_status_isOpen = Launcher.controller.isVolunteerCollectionOpen();
    private final List<VolontarioRowView> righe = new ArrayList<>();

    @FXML VBox container;
    @FXML Label status;
    @FXML Label stats;
    @FXML TextField ambitoField;
    @FXML IntegerInputField numMaxField;

    private Date controllerDate;

    @FXML private void initialize() {
        controllerDate = Launcher.controller.date;
        setupStatusLabel();
        numMaxField.setValue(getNumMax());
        ambitoField.setText(getAmbito());
        buildView();
        refreshStats();
    }

    private void setupStatusLabel() {
        if (actual_status_isOpen) {
            status.getStyleClass().remove("error-label");
            status.getStyleClass().add("success-label");
            status.setText("APERTO");
        } else {
            status.getStyleClass().remove("success-label");
            status.getStyleClass().add("error-label");
            status.setText("CHIUSO");
        }
    }

    private void buildView() {
            container.getChildren().clear();
            righe.clear();
    
            // 1) Calcolo del mese successivo e numero di giorni
            Month yM = controllerDate.clone().addMonth(1).getMonth();
            int daysInMonth = yM.maxLength();
    
            // 2) Creazione dell'HBox di intestazione
            HBox header = new HBox(5);
            header.getStyleClass().add("vol-row");  // stessa classe usata per le righe normali
    
            // 2.a) Prima colonna dell'intestazione: nome del mese e anno
            String monthName = yM.getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.getDefault()
            ).toUpperCase() + " " + controllerDate.getYear();
            Label lblMonth = new Label(monthName);
            lblMonth.setPrefWidth(140);
            header.getChildren().add(lblMonth);
    
            // 2.b) Colonne con i numeri dei giorni (1, 2, 3, …, fino a daysInMonth)
            for (int day = 1; day <= daysInMonth; day++) {
                Label lblDay = new Label(String.format("%02d", day));
                lblDay.getStyleClass().addAll("day-box", "header-day");
                HBox.setHgrow(lblDay, Priority.ALWAYS);
                lblDay.setMaxWidth(Double.MAX_VALUE);
                header.getChildren().add(lblDay);
            }
    
            // Aggiungo l'intestazione al container
            container.getChildren().add(header);
    
            // 3) Recupero e filtraggio dei volontari
            List<Volontario> lista = getVolontariUsabili();
    
            // 4) Per ciascun volontario, creo VolontarioRowView e la aggiungo
            for (Volontario v : lista) {
                VolontarioRowView row = new VolontarioRowView(v, daysInMonth);
                righe.add(row);
                container.getChildren().add(row.getNode());
            }
        }
    

    public void refreshData() {
        for (VolontarioRowView row : righe) {
            row.update();
        }
        refreshStats();
    }

    @FXML private void onOpenCollection() {
        if (actual_status_isOpen) return;
        
        Payload<?> res = Launcher.controller.interpreter("collection -o");
        if (res == null || res.getStatus() != Status.INFO) {
            Launcher.toast(res);
            return;
        }
        
        actual_status_isOpen = true;
        status.getStyleClass().remove("error-label");
        status.getStyleClass().add("success-label");
        status.setText("APERTO");
    }

    @FXML private void onCloseCollection() {
        if (!actual_status_isOpen) return;
        
        Payload<?> res = Launcher.controller.interpreter("collection -c");
        if (res == null || res.getStatus() != Status.INFO) {
            Launcher.toast(res);
            return;
        }
        
        actual_status_isOpen = false;
        status.getStyleClass().remove("success-label");
        status.getStyleClass().add("error-label");
        status.setText("CHIUSO");
    }

    @FXML private void onMakeOrario() {
        Payload<?> res = Launcher.controller.interpreter("makeplan");
        Launcher.toast(res);
    }

    private void refreshStats() {
        double res = Launcher.controller.getVolontariStats();
        stats.setText(String.format("%.2f%%", res * 100));
    }

    @FXML private void onConfirmNum() {
        if (numMaxField.getValue() == null) return;
        int use = numMaxField.getValue();
        if (use <= 0) return;

        Payload<?> res = Launcher.controller.interpreter("setmax " + use);
        Launcher.toast(res);
    }

    private String getAmbito() {
        return Model.getInstance().appSettings.getAmbitoTerritoriale();
    }

    private int getNumMax() {
        return Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();
    }

    private List<Volontario> getVolontariUsabili() {
        List<Volontario> lista = Model
            .getInstance()
            .dbVolontarioHelper
            .getItems();
        lista.removeIf(v -> !v.isUsable());
        return lista;
    }
}
