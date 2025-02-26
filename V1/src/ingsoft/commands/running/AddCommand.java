package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
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
        app.db.addConfiguratore(args[0], args[1]); // aggiunge un nuovo configuratore che dovrà cambiare psw al primo accesso
    }

    private void addFruitore(String[] args) {
        app.db.addFruitore(args[0], args[1]); // aggiunge un nuovo fruitore che dovrà cambiare psw al primo accesso
    }

    private void addVolontario(String[] args) {
        app.db.addVolontario(args[0], args[1]); // aggiunge un nuovo volontario che dovrà cambiare psw al primo accesso
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
