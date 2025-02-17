package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.ViewSE;

public class RemoveCommand extends AbstractCommand {

    public RemoveCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
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
            ViewSE.print("Errore nell'utilizzo del comando 'remove'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> {
                ViewSE.print("Eseguo: Rimuovo configuratore");
                removeConfiguratore(args);
            }
            // Qui inserisci la logica per rimuovere un configuratore
            case 'f' -> {
                ViewSE.print("Eseguo: Rimuovo fruitore");
                removeFruitore(args);
            }
            // Logica per rimuovere un fruitore
            case 'v' -> {
                ViewSE.print("Eseguo: Rimuovo volontario");
                removeVolontario(args);
            }
            // Logica per rimuovere un volontario
            default -> ViewSE.print("Opzione non riconosciuta per 'remove'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void removeConfiguratore(String[] args){
        app.db.removeConfiguratore(args[0]);  //rimuove un configuratore
    }

    private void removeFruitore(String[] args){
        app.db.removeFruitore(args[0]);   //rimuove un fruitore
    }

    private void removeVolontario(String[] args){
        app.db.removeVolontario(args[0]);     //rimuove un volontario
    }
}
