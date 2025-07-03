package GUI.it.proj.utils;

import com.dlsc.gemsfx.CalendarView;
import com.dlsc.gemsfx.CalendarView.SelectionModel.SelectionMode;
import V5.Ingsoft.util.Date;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Consumer;

public class Calendar {
    private final CalendarView calendarView;
    private final VBox view;
    private Date targetDate;
//     private final Consumer<List<LocalDate>> onConfirm;

    /**
     * @param d data di riferimento per il calendario (anno/mese)
     * @param onConfirm callback invocata quando si preme il bottone, riceve la lista di LocalDate selezionate
     */
    public Calendar(Date d, Consumer<List<LocalDate>> onConfirm) {
        this.targetDate = d;
        // this.onConfirm = onConfirm;

        calendarView = new CalendarView();
        calendarView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE_DATES);
        calendarView.setMarkSelectedDaysOfPreviousOrNextMonth(false);
        calendarView.setShowMonthArrows(false);
        calendarView.setMonthSelectionViewEnabled(false);
        calendarView.setYearSelectionViewEnabled(false);
        // YearMonth.of: assicurati che getMonth() restituisca 1–12
        calendarView.setYearMonth(YearMonth.of(targetDate.getYear(), targetDate.getMonth()));

        // Se vuoi un listener immediato, puoi tenerlo; altrimenti saltalo.
        // Qui non registriamo listener immediato, ci limitiamo al bottone.

        Button confirmButton = new Button("Conferma Selezione");
        confirmButton.setOnAction(e -> {
            onConfirm.accept(getSelected());
        });

        view = new VBox(10, calendarView, confirmButton);
        view.setAlignment(Pos.CENTER);
        view.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(calendarView, Priority.ALWAYS);
        calendarView.setMaxWidth(Double.MAX_VALUE);
    }

    public VBox getView() {
        return view;
    }

    /** Se vuoi aggiornare il mese di riferimento in runtime */
    public void setTargetDate(Date d) {
        if (d != null && (d.getYear() != targetDate.getYear() || d.getMonth() != targetDate.getMonth())) {
            this.targetDate = d;
            calendarView.setYearMonth(YearMonth.of(d.getYear(), d.getMonth()));
        }
    }

    /** Restituisce le date selezionate; di default tutte, oppure filtra sul mese target se necessario */
    public List<LocalDate> getSelected() {
        // Se vuoi inviare tutte le selezioni, basta:
        List<LocalDate> all = List.copyOf(calendarView.getSelectionModel().getSelectedDates());
        // Se invece vuoi filtrare su targetDate.getMonth():
        //Month m = Month.of(targetDate.getMonth());
        //return all.stream().filter(ld -> ld.getMonth() == m).collect(Collectors.toList());
        return all;
    }

    public void setSelected(List<Date> dates) {
        calendarView.getSelectionModel().clearSelection();

        if(dates == null) return;
        
        for (Date date : dates) {
                calendarView.getSelectionModel().select(date.localDate.toLocalDate());
        }
    }

    public void setSingleSelected(Date set) {
        if(set == null) return;
        
        calendarView.getSelectionModel().select(set.localDate.toLocalDate());
    }
}
