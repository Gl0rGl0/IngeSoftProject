package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;

public class AddCommandSETUP extends AbstractCommand {

    public AddCommandSETUP(App app, CommandListSETUP commandInfo) {
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
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (deve essere 'L')
        if (options.length < 1) {
            ViewSE.print("Errore nell'utilizzo del comando 'add'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'L' -> addLuoghi(args);
            default -> ViewSE.print("Opzione non riconosciuta per 'add' duratne il SETUP.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }


    private void addLuoghi(String[] args){
        String[] a = StringUtils.joinQuotedArguments(args);

        //app.db addLuoghi
    }
}
