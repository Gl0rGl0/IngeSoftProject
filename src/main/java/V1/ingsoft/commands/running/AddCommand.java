package V1.ingsoft.commands.running;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AbstractCommand;
import V1.ingsoft.luoghi.TipoVisita;
import V1.ingsoft.persone.Volontario;
import V1.ingsoft.util.AssertionControl;
import V1.ingsoft.util.Date;
import V1.ingsoft.util.StringUtils;

public class AddCommand extends AbstractCommand {
    private final App app;
    private final String CLASSNAME = this.getClass().getSimpleName();

    public AddCommand(App app) {
        this.app = app;
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
                    app.getCurrentUser().getUsername() + "| Errore nell'utilizzo del comando 'add'", 2, CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> addConfiguratore(args);
            case 'f' -> addFruitore(args);
            case 'v' -> addVolontario(args);
            case 'L' -> addLuogo(args);
            case 'V' -> addVisita(args);
            case 't' -> addTipoVisita(args);
            default -> ViewSE.println("Opzione non riconosciuta per 'add'.");
        }
    }

    private void addConfiguratore(String[] args) {
        // aggiunge un nuovo configuratore che dovrà cambiare psw al primo accesso
        if (app.db.addConfiguratore(args[0], args[1])) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Aggiunto configuratore: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non aggiunto configuratore: " + args[0], 2, CLASSNAME);
        }
    }

    private void addFruitore(String[] args) {
        // aggiunge un nuovo fruitore che dovrà cambiare psw al primo accesso
        if (app.db.addFruitore(args[0], args[1])) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Aggiunto fruitore: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non aggiunto fruitore: " + args[0], 2, CLASSNAME);
        }
    }

    private void addVolontario(String[] args) {
        // aggiunge un nuovo volontario che dovrà cambiare psw al primo accesso
        if (app.db.addVolontario(args[0], args[1])) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Aggiunto volontario: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non aggiunto volontario: " + args[0], 2, CLASSNAME);
        }
    }

    private void addTipoVisita(String[] args) {
        if (!app.alreadyDone16) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername()
                            + "| Non puoi aggiungere un tipo di visita se non è il 16 del month: " + args[0],
                    1,
                    CLASSNAME);
            return;
        }

        String[] a = StringUtils.joinQuotedArguments(args);
        app.db.addTipoVisita(a, app.date);
    }

    private void addLuogo(String[] args) {
        if (!app.alreadyDone16) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non puoi aggiungere un luogo se non è il 16 del month: "
                            + args[0],
                    1,
                    CLASSNAME);
            return;
        }
        // NON PUOI USARLO ADESSO, ASPETTA LA V3...

        // String[] a = StringUtils.joinQuotedArguments(args);
        // app.db.addLuogo(a[0], a[1], new GPS(a[2]));
    }

    private void addVisita(String[] args) {
        if (!app.alreadyDone16) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername()
                            + "| Non puoi aggiungere una visita al calendario se non è il 16 del month",
                    1,
                    CLASSNAME);
            return;
        }

        // 1. deve esistere la visita
        // 2. deve esistere il volontario
        // 3. il volontario deve essere disponibile quel giorno
        // 4. la visita deve essere possibile quel giorno
        // 5. il volontario non deve avere altre visite
        // 6. la visita deve essere organizzata solo una volta al giorno

        // USAGE add -V [type] [hVisita] [nameVolontario]
        String[] a = StringUtils.joinQuotedArguments(args);
        TipoVisita tv = app.db.getTipoVisitaByName(a[0]);
        Date d = new Date(a[1]);
        Volontario v = app.db.findVolontario(a[2]);

        if (tv == null) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Nessun tipo di visita con quel name: " + a[0], 1,
                    CLASSNAME);
            return;
        }

        if (v == null) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Nessun tipo di visita con quel name: " + a[1], 1,
                    CLASSNAME);
            return;
        }

        if (!tv.getVolontariUIDs().contains(v.getUsername())) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Il volontario non è assegnato a quel tipo di visita", 2,
                    CLASSNAME);
            return;
        }

        if (!v.getAvailability()[d.getDay()]) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername()
                            + "| Il volontario non è disponibile quel giorno per quella visita",
                    2,
                    CLASSNAME);
            return;
        }

        if (!tv.getDays().contains(d.dayOfTheWeek())) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername()
                            + "| La visita non può essere svolta quel giorno della settimana",
                    2,
                    CLASSNAME);
            return;
        }

    }
}
