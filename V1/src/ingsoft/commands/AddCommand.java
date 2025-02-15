package ingsoft.commands;

import ingsoft.App;
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
            // Qui inserisci la logica per aggiungere un configuratore
            case 'f' -> addFruitore(args);
            // Logica per aggiungere un fruitore
            case 'v' -> addVolontario(args);
            case 'L' -> addLuoghi(args);
            case 'V' -> addVisite(args);
            // Logica per aggiungere un volontario
            default -> ViewSE.print("Opzione non riconosciuta per 'add'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void addConfiguratore(String[] args){
        app.db.addConfiguratore(args[0], args[1]);
    }

    private void addFruitore(String[] args){
        app.db.addFruitore(args[0], args[1]);
    }

    private void addVolontario(String[] args){
        app.db.addVolontario(args[0], args[1]);
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
