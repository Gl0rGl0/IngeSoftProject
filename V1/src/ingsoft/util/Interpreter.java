package ingsoft.util;

import ingsoft.commands.Command;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;

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
            ViewSE.print("Errore: nessun comando fornito.");
            ViewSE.log("V1 ERRORE NESSUN COMANDO", "GRAVE");
            return;
        }
        
        String cmd = tokens[0];
        ArrayList<String> optionsList = new ArrayList<>();
        ArrayList<String> argsList = new ArrayList<>();
        
        // Separa opzioni (token che iniziano con '-') e argomenti
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
            int userPerm = currentUser.type().getPriorita();
            // Controlla i permessi
            if (!command.canPermission(userPerm)) {
                ViewSE.print("Non hai i permessi necessari per eseguire il comando '" + cmd + "'.");
                return;
            }
            // Se l'utente è in stato di "firstAccess", limita i comandi eseguibili
            if (currentUser.firstAccess() &&
                    !command.canPermission(PersonaType.CAMBIOPSW.getPriorita())) {
                ViewSE.print("Non hai i permessi necessari per eseguire il comando '" + cmd +
                        "' finché non viene cambiata la password con 'changepsw [nuovapsw]'.");
                return;
            }
            // Esegue il comando
            command.execute(options, args);
        } else {
            ViewSE.print("\"" + cmd + "\" non è riconosciuto come comando interno.");
        }
    }

    public boolean haveAllBeenExecuted(){
        boolean out = false;
        for (Command c : commandRegistry.values()) {
            out &= c.hasBeenExecuted();
        }
        return out;
    }
}