package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;

public class RemoveCommand extends AbstractCommand {

    private final App app;
    public RemoveCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.REMOVE;
    }

    @Override
    /**
     * Implementazione del comando "remove".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (es. "c" per configuratore)
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'remove'");
            return;
        }
        char option = options[0].charAt(0);

        
        String[] ar = StringUtils.joinQuotedArguments(args);

        switch (option) {
            case 'c' -> {
                ViewSE.println("Eseguo: Rimuovo configuratore");
                removeConfiguratore(ar);
            }
            // Qui inserisci la logica per rimuovere un configuratore
            case 'f' -> {
                ViewSE.println("Eseguo: Rimuovo fruitore");
                removeFruitore(ar);
            }
            // Logica per rimuovere un fruitore
            case 'v' -> {
                ViewSE.println("Eseguo: Rimuovo volontario");
                removeVolontario(ar);
            }

            case 'L' -> removeLuogo(ar);
            // Logica per rimuovere un volontario
            default -> ViewSE.println("Opzione non riconosciuta per 'remove'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void removeConfiguratore(String[] args){
        ViewSE.println("Eseguo: Rimuovo configuratore");
        app.db.removeConfiguratore(args[0]);  //rimuove un configuratore
    }

    private void removeFruitore(String[] args){
        ViewSE.println("Eseguo: Rimuovo fruitore");
        app.db.removeFruitore(args[0]);   //rimuove un fruitore
    }

    private void removeVolontario(String[] args){
        ViewSE.println("Eseguo: Rimuovo volontario");
        app.db.removeVolontario(args[0]);     //rimuove un volontario
    }

    private void removeLuogo(String[] args){
        ViewSE.println("Eseguo: Rimuovo luogo");
        app.db.removeLuogo(args[0]);
    }
}
