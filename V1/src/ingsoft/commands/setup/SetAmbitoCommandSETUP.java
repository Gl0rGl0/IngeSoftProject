package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.ViewSE;
public class SetAmbitoCommandSETUP extends AbstractCommand {

    public SetAmbitoCommandSETUP(App app, CommandListSETUP commandInfo) {
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
        if(args.length < 1){
            ViewSE.print("Errore nell'utilizzo del comando, richiesto almeno un nome.");
        }
        try {
            StringBuilder set = new StringBuilder();
            for (String s : args) {
                set.append(s).append(" ");
            }
            app.ambitoTerritoriale = set.toString();
        } catch (Exception e) {
            //
        }
    }
}
