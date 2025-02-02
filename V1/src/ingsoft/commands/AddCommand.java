package ingsoft.commands;

import ingsoft.App;
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
            ViewSE.log("Errore nell'utilizzo del comando 'add': " + CommandList.ADD);
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> ViewSE.log("Eseguo: Aggiunta configuratore");
            // Qui inserisci la logica per aggiungere un configuratore
            case 'f' -> ViewSE.log("Eseguo: Aggiunta fruitore");
            // Logica per aggiungere un fruitore
            case 'v' -> ViewSE.log("Eseguo: Aggiunta volontario");
            // Logica per aggiungere un volontario
            default -> ViewSE.log("Opzione non riconosciuta per 'add'. Uso: " + CommandList.ADD);
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }
}
