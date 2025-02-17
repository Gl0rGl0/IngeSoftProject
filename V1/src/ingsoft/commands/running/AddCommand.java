package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;

public class AddCommand extends AbstractCommand {

    public AddCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (es. "c" per configuratore)
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
            case 'V' -> addVisite(args);
            default -> ViewSE.print("Opzione non riconosciuta per 'add'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void addConfiguratore(String[] args){
        app.db.addConfiguratore(args[0], args[1]);  //aggiunge un nuovo configuratore che dovrà cambiare psw al primo accesso
    }

    private void addFruitore(String[] args){
        app.db.addFruitore(args[0], args[1]);   //aggiunge un nuovo fruitore che dovrà cambiare psw al primo accesso
    }

    private void addVolontario(String[] args){
        app.db.addVolontario(args[0], args[1]);     //aggiunge un nuovo volontario che dovrà cambiare psw al primo accesso
    }

    private void addVisite(String[] args){
        String[] a = StringUtils.joinQuotedArguments(args);
        //app.db.addVisite
    }

    private void addLuoghi(String[] args){
        String[] a = StringUtils.joinQuotedArguments(args);
        
        if (app.db.dbLuoghiHelper.finalized){
            ViewSE.print("Non puoi eseguire questa azione adesso, il DB è già inizializzato.");
            return;
        }

        //app.db addLuoghi
    }
}
