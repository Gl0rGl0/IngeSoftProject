package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.StatusItem;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;

import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MakePlanCommand extends AbstractCommand {

    public MakePlanCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MAKEPLAN;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (controller == null) {
            AssertionControl.logMessage("Controller cannot be null in MakePlanCommand", 1, CLASSNAME);
            return; // Cannot proceed
        }
        makeorario();
    }

    private void makeorario() {
        if (controller == null || controller.date == null || controller.getDB() == null) {
            AssertionControl.logMessage("Controller dependencies (date, db) cannot be null in makeorario", 1, CLASSNAME);
            return; // Cannot proceed
        }

        if (!isExecutable())
            return;

        ArrayList<TipoVisita> tipi = controller.db.dbTipoVisiteHelper.getTipoVisiteIstanziabili();
        tipi.sort(Comparator.comparingInt(t -> t.getInitTime().getMinutes()));

        Month month = controller.date.getMonth().plus(1);
        int year = controller.date.getYear();
        int daysInMonth = month.maxLength();

        for (int day = 1; day <= daysInMonth; day++) {
            Date toCheckDate = new Date(day, month.getValue(), year);
            processVisitsForDate(toCheckDate, tipi);
        }
    }

    /**
     * Processes visits for a specific date.
     */
    private void processVisitsForDate(Date date, List<TipoVisita> tipi) {
        if (date == null) {
            AssertionControl.logMessage("Date cannot be null in processVisitsForDate", 1, CLASSNAME);
            return; // Cannot process without a date
        }
        if (tipi == null) {
            AssertionControl.logMessage("TipoVisita list cannot be null in processVisitsForDate", 1, CLASSNAME);
            return; // Cannot process without visit types
        }
        for (TipoVisita tv : tipi) {
            if (tv == null) {
                AssertionControl.logMessage("Null TipoVisita found in list during plan generation for date: " + date, 1, CLASSNAME);
                continue; // Skip this null entry
            }
            if (!isVisitEligibleOnDate(tv, date))
                continue;

            assignVisitForEligibleVolunteers(tv, date);
        }
    }

    /**
     * Checks if a visit is eligible for the date:
     * - Status ACTIVE (Proposed)
     * - The day of the week of the date is present among those of the visit
     * - The date is not precluded
     * - The date is in the temporal arc of the type
     */
    private boolean isVisitEligibleOnDate(TipoVisita visita, Date date) {
        // A TipoVisita definition doesn't have a PROPOSED status; the generated Visita does.
        // Eligibility depends on the definition (e.g., active period, etc. - assuming basic checks here)
        // and the date constraints.
        return visita.getStatus() == StatusItem.ACTIVE
                && visita.getDays().contains(date.dayOfTheWeek())
                && !controller.db.dbDatesHelper.getPrecludedDates().contains(date)
                && Date.between(visita.getInitDay(), date, visita.getFinishDay());
    }

    /**
     * For an eligible visit, checks and assigns the visit to available volunteers
     * who do not have conflicts.
     */
    private void assignVisitForEligibleVolunteers(TipoVisita visita, Date date) {
        for (String volontarioUID : visita.getVolontariUIDs()) {
            Volontario volontario = controller.db.dbVolontarioHelper.getPersona(volontarioUID);
            if (volontario == null)
                continue;

            // Check volunteer availability for the current day
            if (!volontario.isAvailabile(date.getDay()))
                continue;

            // Check conflicts for the volunteer and the date
            if (controller.db.dbVisiteHelper.volontarioHaConflitto(volontario, date, visita)) // volunteerHasConflict
                continue;

            // If all checks pass, assign the visit
            controller.db.dbVisiteHelper.addVisita(new Visita(visita, date, volontarioUID));
        }
    }
}
