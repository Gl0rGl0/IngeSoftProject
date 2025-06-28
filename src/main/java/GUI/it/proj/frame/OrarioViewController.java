package GUI.it.proj.frame;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.dlsc.unitfx.IntegerInputField;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Calendar;
import GUI.it.proj.utils.Calendarable;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class OrarioViewController implements Calendarable{
    public static final String ID = "orario";

    private boolean actual_status_isOpen = Launcher.getInstance().controller.isVolunteerCollectionOpen();
    private final List<VolontarioRowView> righe = new ArrayList<>();

    @FXML VBox container;
    @FXML Label status;
    @FXML Label stats;
    @FXML TextField ambitoField;
    @FXML IntegerInputField numMaxField;
    @FXML StackPane calendar;

    private Date controllerDate;
    private Calendar calendarComponent;

    @FXML private void initialize() {
        controllerDate = Launcher.getInstance().controller.date;
        setupStatusLabel();
        numMaxField.setValue(getNumMax());
        ambitoField.setText(getAmbito());
        buildView();

        calendarComponent = new Calendar(Launcher.getInstance().controller.date.clone().addMonth(2), dates -> {
            updateCalendar(dates);
        });

        calendar.getChildren().add(calendarComponent.getView());
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
        refreshCalendar();
    }

    private void refreshCalendar() {
        List<Date> dates = Model.getInstance().dbDatesHelper.getItems();
        calendarComponent.setSelected(dates);
    }

    @FXML private void onOpenCollection() {
        if (actual_status_isOpen) return;
        
        Payload<?> res = Launcher.getInstance().controller.interpreter("collection -o");
        if (res == null || res.getStatus() != Status.INFO) {
            Launcher.getInstance().toast(res);
            return;
        }
        
        actual_status_isOpen = true;
        status.getStyleClass().remove("error-label");
        status.getStyleClass().add("success-label");
        status.setText("APERTO");
    }

    @FXML private void onCloseCollection() {
        if (!actual_status_isOpen) return;
        
        Payload<?> res = Launcher.getInstance().controller.interpreter("collection -c");
        if (res == null || res.getStatus() != Status.INFO) {
            Launcher.getInstance().toast(res);
            return;
        }
        
        actual_status_isOpen = false;
        status.getStyleClass().remove("success-label");
        status.getStyleClass().add("error-label");
        status.setText("CHIUSO");
    }

    @FXML private void onMakeOrario() {
        Payload<?> res = Launcher.getInstance().controller.interpreter("makeplan");
        Launcher.getInstance().toast(res);
    }

    @FXML private void onConfirmNum() {
        if (numMaxField.getValue() == null) return;
        int use = numMaxField.getValue();
        if (use <= 0) return;

        Payload<?> res = Launcher.getInstance().controller.interpreter("setmax " + use);
        Launcher.getInstance().toast(res);
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

    @Override
    public void updateCalendar(List<LocalDate> dates) {
        StringBuilder out = new StringBuilder();
        for (LocalDate localDate : dates) {
            out.append(String.format(" %s/%s/%s", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear()));
        }
        
        if(dates.isEmpty()){
            Model.getInstance().dbDatesHelper.clear();
            Launcher.getInstance().toast(Payload.info("Cleared all precluded dates", "Cleared all precluded dates"));
            return;
        }

        Payload<?> res = Launcher.getInstance().controller.interpreter("preclude -b " + out);
        Launcher.getInstance().toast(res);
    }
}
