package V1.ingsoft.util;

import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.Command;
import V1.ingsoft.persone.Persona;
import V1.ingsoft.persone.PersonaType;

import java.util.ArrayList;
import java.util.Map;

public class Interpreter {
    // Mappa dei comandi passata durante il setup
    private Map<String, Command> commandRegistry;

    // Costruttore che riceve la mappa dei comandi
    public Interpreter(Map<String, Command> commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    // Permette di aggiornare la mappa dei comandi in caso di necessità
    public void setCommandRegistry(Map<String, Command> commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    /**
     * Interpreta il prompt fornito dall'utente e, in base alla mappa dei comandi,
     * esegue l'azione corrispondente.
     *
     * @param prompt      la stringa di comando immessa
     * @param currentUser l'utente corrente (necessario per controllare i permessi)
     */
    public void interpret(String prompt, Persona currentUser) {
        String[] tokens = prompt.trim().split("\\s+");

        if (tokens.length == 0 || tokens[0].isEmpty()) {
            AssertionControl.logMessage("ERRORE NESSUN COMANDO", 2, this.getClass().getSimpleName());
            ViewSE.println("Errore: nessun comando fornito.");
            return;
        }

        String cmd = tokens[0];
        ArrayList<String> optionsList = new ArrayList<>();
        ArrayList<String> argsList = new ArrayList<>();

        // Separa options (token che iniziano con '-') e argomenti
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("-") && token.length() > 1) {
                optionsList.add(token.substring(1));
            } else {
                argsList.add(token);
            }
        }

        String[] options = optionsList.toArray(String[]::new);
        String[] args = argsList.toArray(String[]::new);

        Command command = commandRegistry.get(cmd);
        if (command != null) {
            int userPriority = currentUser.getType().getPriority();
            // Controlla i permessi
            if (!command.canBeExecutedBy(userPriority)) {
                ViewSE.println("Non hai i permessi necessari per eseguire il comando '" + cmd + "'.");
                return;
            }
            // Se l'utente è in stato di "firstAccess", limita i comandi eseguibili
            if (currentUser.isNew() &&
                    !command.canBeExecutedBy(PersonaType.CAMBIOPSW.getPriority())) {
                ViewSE.println("Non hai i permessi necessari per eseguire il comando '" + cmd +
                        "' finché non viene cambiata la password con 'changepsw [nuovapsw]'.");
                return;
            }
            // Esegue il comando
            command.execute(options, args);
        } else {
            ViewSE.println("\"" + cmd + "\" non è riconosciuto come comando interno.");
        }
    }

    public boolean haveAllBeenExecuted() {
        boolean out = true;
        for (Command c : commandRegistry.values()) {
            out &= c.hasBeenExecuted();
        }
        return out; // Ne basta una false per dare false come risultato
    }
}