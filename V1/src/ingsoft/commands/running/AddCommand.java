package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.AbstractCommand;
import ingsoft.luoghi.TipoVisita;
import ingsoft.persone.Volontario;
import ingsoft.util.AssertionControl;
import ingsoft.util.Date;
import ingsoft.util.StringUtils;

public class AddCommand extends AbstractCommand {
    private final App app;
    private final String NOMECLASSE = this.getClass().getSimpleName();

    public AddCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.ADD;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'add'");
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Errore nell'utilizzo del comando 'add'", 2, NOMECLASSE);
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> addConfiguratore(args);
            case 'f' -> addFruitore(args);
            case 'v' -> addVolontario(args);
            case 'L' -> addLuoghi(args);
            case 'V' -> addVisita(args);
            case 't' -> addTipoVisita(args);
            default -> ViewSE.println("Opzione non riconosciuta per 'add'.");
        }
    }

    private void addConfiguratore(String[] args) {
        // aggiunge un nuovo configuratore che dovrà cambiare psw al primo accesso
        if (app.db.addConfiguratore(args[0], args[1])) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Aggiunto configuratore: " + args[0], 3, NOMECLASSE);
        } else {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non aggiunto configuratore: " + args[0], 2, NOMECLASSE);
        }
    }

    private void addFruitore(String[] args) {
        // aggiunge un nuovo fruitore che dovrà cambiare psw al primo accesso
        if (app.db.addFruitore(args[0], args[1])) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Aggiunto fruitore: " + args[0], 3, NOMECLASSE);
        } else {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non aggiunto fruitore: " + args[0], 2, NOMECLASSE);
        }
    }

    private void addVolontario(String[] args) {
        // aggiunge un nuovo volontario che dovrà cambiare psw al primo accesso
        if (app.db.addVolontario(args[0], args[1])) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Aggiunto volontario: " + args[0], 3, NOMECLASSE);
        } else {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non aggiunto volontario: " + args[0], 2, NOMECLASSE);
        }
    }

    private void addTipoVisita(String[] args) {
        if (!app.alreadyDone16) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername()
                            + "| Non puoi aggiungere un tipo di visita se non è il 16 del mese: " + args[0],
                    1,
                    NOMECLASSE);
            return;
        }

        String[] a = StringUtils.joinQuotedArguments(args);
        app.db.aggiungiTipoVisita(a, app.date);
    }

    private void addLuoghi(String[] args) {
        if (!app.alreadyDone16) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Non puoi aggiungere un luogo se non è il 16 del mese: "
                            + args[0],
                    1,
                    NOMECLASSE);
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
                            + "| Non puoi aggiungere una visita al calendario se non è il 16 del mese",
                    1,
                    NOMECLASSE);
            return;
        }

        // USAGE add -V [nomeVisita] [hVisita] [nomeVolontario]
        String[] a = StringUtils.joinQuotedArguments(args);
        TipoVisita tv = app.db.getTipoVisitaByName(a[0]);
        Date d = new Date(a[1]);
        Volontario v = app.db.findVolontario(a[2]);

        if (tv == null) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Nessun tipo di visita con quel nome: " + a[0], 1,
                    NOMECLASSE);
            return;
        }

        if (v == null) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Nessun tipo di visita con quel nome: " + a[1], 1,
                    NOMECLASSE);
            return;
        }

        if (d.getOra().equals("00:00")) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Formato di ora sbagliato inserito: " + a[1], 1,
                    NOMECLASSE);
            return;
        }

        if (!tv.getVolontariUID().contains(v.getUsername())) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername() + "| Il volontario non è assegnato a quel tipo di visita", 2,
                    NOMECLASSE);
            return;
        }

        if (!v.getDisponibilita()[d.getGiorno()]) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername()
                            + "| Il volontario non è disponibile quel giorno per quella visita",
                    2,
                    NOMECLASSE);
            return;
        }

        if (!tv.getGiorni().contains(d.giornoDellaSettimana())) {
            AssertionControl.logMessage(
                    app.getCurrentUser().getUsername()
                            + "| La visita non può essere svolta quel giorno della settimana",
                    2,
                    NOMECLASSE);
            return;
        }

    }
}
