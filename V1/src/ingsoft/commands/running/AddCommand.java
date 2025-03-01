package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.AssertionControl;
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
            AssertionControl.logMessage("Errore nell'utilizzo del comando 'add'", 2, this.getClass().getSimpleName());
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> addConfiguratore(args);
            case 'f' -> addFruitore(args);
            case 'v' -> addVolontario(args);
            case 'L' -> {if(app.date.getGiorno() == 16) addLuoghi(args); else ViewSE.println("Azione possibile solo il 16 del mese!");}
            case 't' -> addTipoVisita(args);
            default -> ViewSE.println("Opzione non riconosciuta per 'add'.");
        }
    }

    private void addConfiguratore(String[] args) {
        // aggiunge un nuovo configuratore che dovrà cambiare psw al primo accesso
        if (app.db.addConfiguratore(args[0], args[1])) {
            AssertionControl.logMessage("Aggiunto configuratore: " + args[0], 3, this.getClass().getSimpleName());
        } else {
            AssertionControl.logMessage("Non aggiunto configuratore: " + args[0], 2, this.getClass().getSimpleName());
        }
    }

    private void addFruitore(String[] args) {
        // aggiunge un nuovo fruitore che dovrà cambiare psw al primo accesso
        if (app.db.addFruitore(args[0], args[1])) {
            AssertionControl.logMessage("Aggiunto fruitore: " + args[0], 3, this.getClass().getSimpleName());
        } else {
            AssertionControl.logMessage("Non aggiunto fruitore: " + args[0], 2, this.getClass().getSimpleName());
        }
    }

    private void addVolontario(String[] args) {
        // aggiunge un nuovo volontario che dovrà cambiare psw al primo accesso
        if (app.db.addVolontario(args[0], args[1])) {
            AssertionControl.logMessage("Aggiunto volontario: " + args[0], 3, this.getClass().getSimpleName());
        } else {
            AssertionControl.logMessage("Non aggiunto volontario: " + args[0], 2, this.getClass().getSimpleName());
        }
    }

    private void addTipoVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        app.db.aggiungiTipoVisita(a, app.date);
    }

    private void addLuoghi(String[] args) {
        // NON PUOI USARLO ADESSO, ASPETTA LA V3...
        AssertionControl.logMessage("Non puoi aggiungere un luogo prima della V3: " + args[0], 2,
                this.getClass().getSimpleName());

        // String[] a = StringUtils.joinQuotedArguments(args);

        // app.db.addLuogo(a[0], a[1], new GPS(a[2]));
    }
}
