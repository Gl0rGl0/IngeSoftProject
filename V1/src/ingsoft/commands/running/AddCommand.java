package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.AssertionControl;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;

public class AddCommand extends AbstractCommand {

    private final App app;

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
            ViewSE.print("Errore nell'utilizzo del comando 'add'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> addConfiguratore(args);
            case 'f' -> addFruitore(args);
            case 'v' -> addVolontario(args);
            case 'L' -> addLuoghi(args);
            case 't' -> addTipoVisita(args);
            default -> ViewSE.print("Opzione non riconosciuta per 'add'.");
        }
    }

    private void addConfiguratore(String[] args) {
        // aggiunge un nuovo configuratore che dovrà cambiare psw al primo accesso
        if(app.db.addConfiguratore(args[0], args[1])){
            AssertionControl.logMessage("Aggiunto configuratore: " + args[0], 3, this.getClass().getSimpleName());
        }else{
            AssertionControl.logMessage("Non aggiunto configuratore: " + args[0], 2, this.getClass().getSimpleName());
        }
    }

    private void addFruitore(String[] args) {
        // aggiunge un nuovo fruitore che dovrà cambiare psw al primo accesso
        if(app.db.addFruitore(args[0], args[1])){
            AssertionControl.logMessage("Aggiunto fruitore: " + args[0], 3, this.getClass().getSimpleName());
        }else{
            AssertionControl.logMessage("Non aggiunto fruitore: " + args[0], 2, this.getClass().getSimpleName());
        }
    }

    private void addVolontario(String[] args) {
        // aggiunge un nuovo volontario che dovrà cambiare psw al primo accesso
        if(app.db.addVolontario(args[0], args[1])){
            AssertionControl.logMessage("Aggiunto volontario: " + args[0], 3, this.getClass().getSimpleName());
        }else{
            AssertionControl.logMessage("Non aggiunto volontario: " + args[0], 2, this.getClass().getSimpleName());
        }
    }

    private void addTipoVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        app.db.aggiungiTipoVisita(a, app.date);
    }

    private void addLuoghi(String[] args) {
        // NON PUOI USARLO ADESSO, ASPETTA LA V3...
        @SuppressWarnings("unused")
        int niente = args.length;
        // String[] a = StringUtils.joinQuotedArguments(args);

        // app.db.addLuogo(a);
    }
}
