package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.GPS;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;
import java.util.Arrays;

public class AddCommandSETUP extends AbstractCommand {

    private final App app;
    public AddCommandSETUP(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.ADD;
        hasBeenExecuted = false;
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
            ViewSE.print("Errore nell'utilizzo del comando 'add'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'L' -> addLuoghi(args);
            default -> {ViewSE.print("Opzione non riconosciuta per 'add' durante il SETUP."); return;}
        }
        
        this.hasBeenExecuted = true;
    }

    public void addLuoghi(String[] args){
        String[] a = StringUtils.joinQuotedArguments(args);
        System.out.println(Arrays.toString(a));
        String[] vc = new String[args.length - 3];
        for(int i = 3; i < args.length; i++){
            vc[i-3] = args[i];
        }
        app.db.addLuogo(args[0], args[1], new GPS(args[2]), vc);
    }
}
