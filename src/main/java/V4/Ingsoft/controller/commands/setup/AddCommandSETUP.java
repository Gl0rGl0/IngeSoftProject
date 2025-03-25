package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.util.GPS;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class AddCommandSETUP extends AbstractCommand {

    private final Controller controller;

    public AddCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.ADD;
        this.hasBeenExecuted = false;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param options le options (solo -L per i luoghi)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (deve essere
        // 'L')
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'add'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'L' -> addLuogo(args);
            default -> {
                ViewSE.println("Opzione non riconosciuta per 'add' durante il SETUP.");
                return;
            }
        }

        this.hasBeenExecuted = true;
    }

    public void addLuogo(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);

        controller.db.addLuogo(a[0], a[1], new GPS(a[2]));
    }
}
