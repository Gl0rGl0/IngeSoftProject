package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.GPS;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;

public class AddCommandSETUP extends AbstractCommand {

    private final App app;
    public AddCommandSETUP(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.ADD;
        this.hasBeenExecuted = false;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param opzioni le opzioni (solo -L per i luoghi)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (deve essere 'L')
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'add'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'L' -> addLuoghi(args);
            default -> {ViewSE.println("Opzione non riconosciuta per 'add' durante il SETUP."); return;}
        }
        
        this.hasBeenExecuted = true;
    }

    public void addLuoghi(String[] args){
        String[] a = StringUtils.joinQuotedArguments(args);
        
        app.db.addLuogo(a[0], a[1], new GPS(a[2]));
    }
}
