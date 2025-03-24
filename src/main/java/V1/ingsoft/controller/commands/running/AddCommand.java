package V1.ingsoft.controller.commands.running;

import java.util.ArrayList;
import java.util.Comparator;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.AbstractCommand;
import V1.ingsoft.controller.item.luoghi.StatusVisita;
import V1.ingsoft.controller.item.luoghi.TipoVisita;
import V1.ingsoft.controller.item.luoghi.Visita;
import V1.ingsoft.controller.item.persone.Volontario;
import V1.ingsoft.util.AssertionControl;
import V1.ingsoft.util.Date;
import V1.ingsoft.util.StringUtils;
import V1.ingsoft.view.ViewSE;

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
                    controller.getCurrentUser().getUsername() + "| Non aggiunto volontario", 2, CLASSNAME);
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

        controller.db.addTipoVisita(a, controller.date);
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
        // NON PUOI USARLO ADESSO, ASPETTA LA V3...
        // controller.db.addLuogo(a[0], a[1], new GPS(a[2]));
    }

    private void makeorario() {
        if (!controller.canExecute16thAction)
            return;

        ArrayList<TipoVisita> tipi = controller.db.dbTipoVisiteHelper.getTipoVisiteIstanziabili();
        tipi.sort(Comparator.comparingInt(t -> t.getInitTime().getMinutes()));

        for (int i = 1; i <= Date.monthLength(controller.date.getMonth() + 1); i++) {
            Date toOperate = new Date(i, controller.date.getMonth() + 1, controller.date.getYear());
            for (TipoVisita t : tipi) {
                // Verifica che la visita sia in stato PROPOSTA e compatibile con il giorno
                // della settimana
                if (t.getStatus() != StatusVisita.PROPOSTA)
                    continue;
                if (!t.getDays().contains(toOperate.dayOfTheWeek()))
                    continue;

                for (String volontarioUID : t.getVolontariUIDs()) {
                    Volontario v = controller.db.dbVolontarioHelper.getVolontarioByUID(volontarioUID);
                    if (v == null)
                        continue;

                    // Controlla che il volontario sia disponibile nel giorno specifico
                    if (!v.getAvailability()[i])
                        continue;

                    // Aggiungi qui il controllo per verificare se il volontario ha già visite che
                    // si accavallano.
                    if (controller.db.dbVisiteHelper.volontarioHaConflitto(v, toOperate, t))
                        continue;

                    // Se tutti i controlli sono superati, aggiungi la visita
                    controller.db.dbVisiteHelper.addVisita(new Visita(t, toOperate, volontarioUID));
                }
            }
        }

        System.out.println(controller.db.dbVisiteHelper.getVisite());
    }
}
