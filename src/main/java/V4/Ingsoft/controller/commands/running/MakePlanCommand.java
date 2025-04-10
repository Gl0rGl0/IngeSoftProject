package V4.Ingsoft.controller.commands.running;

import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.StatusVisita;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.Date;

public class MakePlanCommand extends AbstractCommand {

    private final Controller controller;

    public MakePlanCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MAKEPLAN;
    }

    @Override
    public void execute(String[] options, String[] args) {
        makeorario();
    }

    private void makeorario() {
        if (!controller.isActionDay16)
            return;

        ArrayList<TipoVisita> tipi = controller.db.dbTipoVisiteHelper.getTipoVisiteIstanziabili();
        tipi.sort(Comparator.comparingInt(t -> t.getInitTime().getMinutes()));

        Month month = controller.date.getMonth().plus(1);
        int year = controller.date.getYear();
        int daysInMonth = month.maxLength();

        for (int day = 1; day <= daysInMonth; day++) {
            Date currentDate = new Date(day, month.getValue(), year);
            processVisitsForDate(currentDate, tipi);
        }
    }

    /**
     * Processes visits for a specific date.
     */
    private void processVisitsForDate(Date date, List<TipoVisita> tipi) {
        for (TipoVisita visita : tipi) {
            if (!isVisitEligibleOnDate(visita, date))
                continue;

            assignVisitForEligibleVolunteers(visita, date);
        }
    }

    /**
     * Checks if a visit is eligible for the date:
     * - Status PROPOSTA (Proposed)
     * - The day of the week of the date is present among those of the visit
     */
    private boolean isVisitEligibleOnDate(TipoVisita visita, Date date) {
        return visita.getStatus() == StatusVisita.PROPOSED
                && visita.getDays().contains(date.dayOfTheWeek())
                && !controller.db.dbDatesHelper.getPrecludedDates().contains(date);
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
            if (!volontario.getAvailability()[date.getDay()])
                continue;

            // Check conflicts for the volunteer and the date
            if (controller.db.dbVisiteHelper.volontarioHaConflitto(volontario, date, visita)) // volunteerHasConflict
                continue;

            // If all checks pass, assign the visit
            controller.db.dbVisiteHelper.addVisita(new Visita(visita, date, volontarioUID));
        }
    }
}
