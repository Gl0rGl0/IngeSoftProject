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
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class AddCommand extends AbstractCommand {
    private final Controller controller;
    private final String CLASSNAME = this.getClass().getSimpleName();

    public AddCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.ADD;
    }

    @Override
    /**
     * Implementation of the "add" command.
     *
     * @param options the options (e.g., -c for configurator)
     * @param args    any additional arguments
     */
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Error using the 'add' command");
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Error using the 'add' command", 2,
                    CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> addConfiguratore(args);
            case 'f' -> addFruitore(args);
            case 'v' -> addVolontario(args);
            case 'L' -> addLuogo(args);
            case 'V' -> makeorario(); // Assuming this generates the schedule (orario)
            case 't' -> addTipoVisita(args);
            default -> ViewSE.println("Option not recognized for 'add'.");
        }
    }

    private void addConfiguratore(String[] args) {
        // adds a new configurator who will need to change password on first login
        if (args.length > 1 && controller.db.dbConfiguratoreHelper.addConfiguratore(args[0], args[1])) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added configurator: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Configurator not added", 2, CLASSNAME);
        }
    }

    private void addFruitore(String[] args) {
        // adds a new fruitore (visitor/user) who will need to change password on first login
        if (args.length > 1 && controller.db.dbFruitoreHelper.addFruitore(args[0], args[1])) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added fruitore: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Fruitore not added", 2, CLASSNAME);
        }
    }

    private void addVolontario(String[] args) {
        // adds a new volunteer who will need to change password on first login
        if (args.length > 1 && controller.db.dbVolontarioHelper.addVolontario(args[0], args[1])) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added volunteer: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Volunteer not added", 3, CLASSNAME);
        }
    }

    private void addTipoVisita(String[] args) {

        String[] a = StringUtils.joinQuotedArguments(args);

        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Cannot add a visit type if it's not the 16th of the month: " + a[0],
                    1,
                    CLASSNAME);
            return;
        }

        if(a.length < 10){
            AssertionControl.logMessage("Comando 'add' errato", 3, getClass().getSimpleName());
            return;
        }

        if(a[0].trim() == ""){
            AssertionControl.logMessage("You can't add an empty title", 3, getClass().getSimpleName());
            return;
        }

        if (controller.db.addTipoVisita(a, controller.date)) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added TipoVisita: " + a[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| TipoVisita not added", 3, CLASSNAME);
        }
    }

    private void addLuogo(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        if(a.length < 3){
            AssertionControl.logMessage("Comando 'add' errato", 3, getClass().getSimpleName());
            return;
        }

        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Cannot add a place if it's not the 16th of the month: "
                            + a[0],
                    1,
                    CLASSNAME);
            return;
        }

        if(a[0].trim() == ""){
            AssertionControl.logMessage("You can't add an empty title", 3, getClass().getSimpleName());
            return;
        }

        // if(a[2].trim() == ""){
        //     AssertionControl.logMessage("You can't add an empty position", 3, getClass().getSimpleName());
        //     return;
        // }

        if (a.length > 2 && controller.db.addLuogo(a[0], a[1], a[2])) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added place: " + a[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Place not added", 3, CLASSNAME);
        }
    }

    private void makeorario() {
        if (!controller.canExecute16thAction)
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
