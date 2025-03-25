package V4.Ingsoft.controller.commands.running;

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
import V4.Ingsoft.util.GPS;
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
     * Implementazione del comando "add".
     *
     * @param options le options (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'add'");
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Errore nell'utilizzo del comando 'add'", 2,
                    CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> addConfiguratore(args);
            case 'f' -> addFruitore(args);
            case 'v' -> addVolontario(args);
            case 'L' -> addLuogo(args);
            case 'V' -> makeorario();
            case 't' -> addTipoVisita(args);
            default -> ViewSE.println("Opzione non riconosciuta per 'add'.");
        }
    }

    private void addConfiguratore(String[] args) {
        // aggiunge un nuovo configuratore che dovrà cambiare psw al primo accesso
        if (controller.db.addConfiguratore(args[0], args[1])) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Aggiunto configuratore: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Non aggiunto configuratore", 2, CLASSNAME);
        }
    }

    private void addFruitore(String[] args) {
        // aggiunge un nuovo fruitore che dovrà cambiare psw al primo accesso
        if (controller.db.addFruitore(args[0], args[1])) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Aggiunto fruitore: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Non aggiunto fruitore", 2, CLASSNAME);
        }
    }

    private void addVolontario(String[] args) {
        // aggiunge un nuovo volontario che dovrà cambiare psw al primo accesso
        if (args.length > 1 && controller.db.addVolontario(args[0], args[1])) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Aggiunto volontario: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Non aggiunto volontario", 3, CLASSNAME);
        }
    }

    private void addTipoVisita(String[] args) {

        String[] a = StringUtils.joinQuotedArguments(args);

        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Non puoi aggiungere un tipo di visita se non è il 16 del month: " + a[0],
                    1,
                    CLASSNAME);
            return;
        }

        if(controller.db.addTipoVisita(a, controller.date)){
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Aggiunto TipoVisita: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Non aggiunto TipoVisita", 3, CLASSNAME);
        }
    }

    private void addLuogo(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Non puoi aggiungere un luogo se non è il 16 del month: "
                            + a[0],
                    1,
                    CLASSNAME);
            return;
        }
        
        if(a.length > 2 && controller.db.addLuogo(a[0], a[1], new GPS(a[2]))){
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Aggiunto luogo: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Non aggiunto luogo", 3, CLASSNAME);
        }
    }

    private void makeorario() {
        if (!controller.canExecute16thAction)
            return;

        ArrayList<TipoVisita> tipi = controller.db.dbTipoVisiteHelper.getTipoVisiteIstanziabili();
        tipi.sort(Comparator.comparingInt(t -> t.getInitTime().getMinutes()));

        int month = controller.date.getMonth() + 1;
        int year = controller.date.getYear();
        int daysInMonth = Date.monthLength(month);

        for (int day = 1; day <= daysInMonth; day++) {
            Date currentDate = new Date(day, month, year);
            processVisitsForDate(currentDate, tipi);
        }

        System.out.println(controller.db.dbVisiteHelper.getVisite());
    }

    /**
     * Elabora le visite per una data specifica.
     */
    private void processVisitsForDate(Date date, List<TipoVisita> tipi) {
        for (TipoVisita visita : tipi) {
            if (!isVisitEligibleOnDate(visita, date))
                continue;

            assignVisitForEligibleVolunteers(visita, date);
        }
    }

    /**
     * Verifica se una visita è eleggibile per la data:
     * - Stato PROPOSTA
     * - Il giorno della settimana della data è presente tra quelli della visita
     */
    private boolean isVisitEligibleOnDate(TipoVisita visita, Date date) {
        return visita.getStatus() == StatusVisita.PROPOSTA
                && visita.getDays().contains(date.dayOfTheWeek());
    }

    /**
     * Per una visita eleggibile, controlla e assegna la visita ai volontari
     * disponibili
     * che non hanno conflitti.
     */
    private void assignVisitForEligibleVolunteers(TipoVisita visita, Date date) {
        for (String volontarioUID : visita.getVolontariUIDs()) {
            Volontario volontario = controller.db.dbVolontarioHelper.getVolontarioByUID(volontarioUID);
            if (volontario == null)
                continue;

            // Verifica la disponibilità del volontario per il giorno corrente
            if (!volontario.getAvailability()[date.getDay()])
                continue;

            // Controlla conflitti per il volontario e la data
            if (controller.db.dbVisiteHelper.volontarioHaConflitto(volontario, date, visita))
                continue;

            // Se tutti i controlli sono superati, assegna la visita
            controller.db.dbVisiteHelper.addVisita(new Visita(visita, date, volontarioUID));
        }
    }

}
