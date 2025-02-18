package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;
public class SetAmbitoCommandSETUP extends AbstractCommand {

    private final App app;

    public SetAmbitoCommandSETUP(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.SETAMBITO;
        hasBeenExecuted = false;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        if(args.length < 1){
            ViewSE.print("Errore nell'utilizzo del comando, richiesto almeno un nome. Utilizzo: \"Nome nome nome\"");
        }
        try {
            String[] a = StringUtils.joinQuotedArguments(args);
                                            // StringBuilder set = new StringBuilder();
                                            // for (String s : args) {
                                            //     set.append(s).append(" ");
                                            // }    
            app.ambitoTerritoriale = a[0];  // set.toString();                          //BOH NEL CASO L'INPUT NON FOSSE [setambito "Parco grotta del..."] mas [setambito Parco grotta del...]
        } catch (Exception e) {
            return;
        }
        this.hasBeenExecuted = true;
    }
}
