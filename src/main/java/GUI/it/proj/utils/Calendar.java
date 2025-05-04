package GUI.it.proj.utils;

import com.dlsc.gemsfx.CalendarView;
import com.dlsc.gemsfx.CalendarView.SelectionModel.SelectionMode;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class Calendar {

        private final CalendarView calendarView;

        public Calendar() {
                calendarView = new CalendarView();
                calendarView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE_DATES);
                calendarView.setMarkSelectedDaysOfPreviousOrNextMonth(false);
                calendarView.setShowMonthArrows(false);
                calendarView.setMonthSelectionViewEnabled(false);
                calendarView.setYearSelectionViewEnabled(false);
                calendarView.setYearMonth(
                                YearMonth.of(LocalDate.now().getYear(), LocalDate.now().plusMonths(1).getMonth()));
        }

        public VBox getView() {
                Button confirmButton = new Button("Conferma Selezione");
                confirmButton.setOnAction(e -> {
                        List<LocalDate> selectedDates = calendarView.getSelectionModel().getSelectedDates();
                        LocalDate currentMonth = LocalDate.now();
                        List<LocalDate> filteredDates = selectedDates.stream()
                                        .filter(date -> date.getMonth() == currentMonth.getMonth())
                                        .collect(Collectors.toList());

                        System.out.println("Giorni selezionati: " + filteredDates);
                });

                VBox vbox = new VBox(10, calendarView, confirmButton);
                vbox.setAlignment(Pos.CENTER);
                vbox.setMaxWidth(Double.MAX_VALUE);
                // 2) Diamo la priorità di crescita orizzontale al calendarView
                VBox.setVgrow(calendarView, Priority.ALWAYS);
                // 3) Lasciamo che anche il CalendarView si allarghi
                calendarView.setMaxWidth(Double.MAX_VALUE);
                return vbox;
        }
}
