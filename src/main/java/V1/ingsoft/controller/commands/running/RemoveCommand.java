package V1.ingsoft.controller.commands.running;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.AbstractCommand;
import V1.ingsoft.util.AssertionControl;
import V1.ingsoft.util.StringUtils;
import V1.ingsoft.view.ViewSE;

public class RemoveCommand extends AbstractCommand {

    private final Controller controller;
    private final String CLASSNAME = this.getClass().getSimpleName();

    public RemoveCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.REMOVE;
    }

    @Override
    /**
     * Implementazione del comando "remove".
     *
     * @param options le options (es. -c per configuratore)
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
            case 'v' -> {
                if (!controller.canExecute16thAction)
                    removeVolontario(ar);
                else
                    ViewSE.println("Azione possibile solo il 16 del month!");
            }
            case 't' -> {
                if (!controller.canExecute16thAction)
                    removeTipoVisita(ar);
                else
                    ViewSE.println("Azione possibile solo il 16 del month!");
            }
            case 'L' -> {
                if (!controller.canExecute16thAction)
                    removeLuogo(ar);
                else
                    ViewSE.println("Azione possibile solo il 16 del month!");
            }
            default -> ViewSE.println("Opzione non riconosciuta per 'remove'.");
        }
    }

    private void removeConfiguratore(String[] args) {
        ViewSE.println("Eseguo: Rimuovo configuratore");
        controller.db.removeConfiguratore(args[0]);
    }

    private void removeFruitore(String[] args) {
        ViewSE.println("Eseguo: Rimuovo fruitore");
        controller.db.removeFruitore(args[0]);
    }

    private void removeVolontario(String[] args) {
        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "|  + Non puoi rimuovere un volontario se non è il 16 del month: " + args[0],
                    1,
                    CLASSNAME);
            return;
        }

        ViewSE.println("Eseguo: Rimuovo volontario");
        controller.db.removeVolontario(args[0]);
    }

    private void removeTipoVisita(String[] args) {
        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "|  + Non puoi rimuovere un tipo di visita se non è il 16 del month: " + args[0],
                    1,
                    CLASSNAME);
            return;
        }

        ViewSE.println("Eseguo: Rimuovo tipo visita");
        controller.db.removeTipoVisita(args[0]);
    }

    private void removeLuogo(String[] args) {
        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "|  + Non puoi rimuovere un luogo se non è il 16 del month: "
                            + args[0],
                    1,
                    CLASSNAME);
            return;
        }

        ViewSE.println("Eseguo: Rimuovo luogo");
        controller.db.removeLuogo(args[0]);
    }
}
