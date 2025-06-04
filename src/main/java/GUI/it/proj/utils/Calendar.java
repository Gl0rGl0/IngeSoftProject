package GUI.it.proj.utils;

import com.dlsc.gemsfx.CalendarView;
import com.dlsc.gemsfx.CalendarView.SelectionModel.SelectionMode;

import GUI.it.proj.Launcher;
import V5.Ingsoft.util.Date;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class Calendar {

        private final CalendarView calendarView;
        private Date controllerDate;

        public Calendar() {
                controllerDate = Launcher.controller.date;

                calendarView = new CalendarView();
                calendarView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE_DATES);
                calendarView.setMarkSelectedDaysOfPreviousOrNextMonth(false);
                calendarView.setShowMonthArrows(false);
                calendarView.setMonthSelectionViewEnabled(false);
                calendarView.setYearSelectionViewEnabled(false);
                calendarView.setYearMonth(YearMonth.of(controllerDate.getYear(), controllerDate.getMonth().plus(1)));
        }

        public VBox getView() {
                Button confirmButton = new Button("Conferma Selezione");
                confirmButton.setOnAction(e -> {
                        List<LocalDate> selectedDates = calendarView.getSelectionModel().getSelectedDates();
                        Month nextMonth = controllerDate.getMonth().plus(1);
                        List<LocalDate> filteredDates = selectedDates.stream()
                                        .filter(date -> date.getMonth() == nextMonth)
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
