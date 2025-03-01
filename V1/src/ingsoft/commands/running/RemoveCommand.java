package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.StringUtils;

public class RemoveCommand extends AbstractCommand {

    private final App app;

    public RemoveCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.REMOVE;
    }

    @Override
    /**
     * Implementazione del comando "remove".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (es. "c" per
        // configuratore)
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'remove'");
            return;
        }
        char option = options[0].charAt(0);

        String[] ar = StringUtils.joinQuotedArguments(args);

        switch (option) {
            case 'c' -> removeConfiguratore(ar);
            case 'f' -> removeFruitore(ar);
            case 'v' -> removeVolontario(ar);
            case 't' -> removeTipoVisita(ar);
            case 'L' -> removeLuogo(ar);
            default -> ViewSE.println("Opzione non riconosciuta per 'remove'.");
        }
    }

    private void removeConfiguratore(String[] args) {
        ViewSE.println("Eseguo: Rimuovo configuratore");
        app.db.removeConfiguratore(args[0]);
    }

    private void removeFruitore(String[] args) {
        ViewSE.println("Eseguo: Rimuovo fruitore");
        app.db.removeFruitore(args[0]);
    }

    private void removeVolontario(String[] args) {
        ViewSE.println("Eseguo: Rimuovo volontario");
        app.db.removeVolontario(args[0]);
    }

    private void removeTipoVisita(String[] args) {
        ViewSE.println("Eseguo: Rimuovo tipo visita");
        app.db.removeTipoVisita(args[0]);
    }

    private void removeLuogo(String[] args) {
        ViewSE.println("Eseguo: Rimuovo luogo");
        app.db.removeLuogo(args[0]);
    }
}
