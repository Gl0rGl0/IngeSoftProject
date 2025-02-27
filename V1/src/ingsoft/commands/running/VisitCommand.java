package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.AssertionControl;
import ingsoft.util.ViewSE;

public class VisitCommand extends AbstractCommand {
    private final App app;

    public VisitCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.ADD; // VISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if(options.length < 1){
            ViewSE.println("Nessun opzione inserita, riprova...");
            AssertionControl.logMessage("Opzioni insufficienti", 2, this.getClass().getSimpleName());
            return;
        }

        if(args.length < 2){
            ViewSE.println("Utilizzo sbagliato del comando 'visit', riprova...");
            AssertionControl.logMessage("Argomenti insufficienti", 2, this.getClass().getSimpleName());
            return;
        }
        
        switch (options[0].charAt(0)) {
            case 'a' -> iscriviAVisita(args);
            case 'r' -> disiscriviVisita(args);
            default -> ViewSE.println("Opzione non riconosciuta per 'myvisit'.");
        }
    }

    private void iscriviAVisita(String[] args) {
        //String[] a = StringUtils.joinQuotedArguments(args);
        // TODO if(app.db.get(args[]))
    }

    private void disiscriviVisita(String[] args) {
        //String userF = app.getCurrentUser().getUsername();
        //TODO
    }
}