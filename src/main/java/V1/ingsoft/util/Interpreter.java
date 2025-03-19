package V1.ingsoft.util;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AssignCommand;
import V1.ingsoft.commands.ChangePswCommand;
import V1.ingsoft.commands.Command;
import V1.ingsoft.commands.ExitCommand;
import V1.ingsoft.commands.LogoutCommand;
import V1.ingsoft.commands.SetPersoneMaxCommand;
import V1.ingsoft.commands.TimeCommand;
import V1.ingsoft.persone.Persona;
import V1.ingsoft.persone.PersonaType;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Interpreter {
    // Mappa dei comandi passata durante il setup
    protected HashMap<String, Command> commandRegistry = new HashMap<>();

    // Costruttore che riceve la mappa dei comandi
    public Interpreter(App controller) {
        commandRegistry.put("time", new TimeCommand(controller));
        commandRegistry.put("logout", new LogoutCommand(controller));
        commandRegistry.put("changepsw", new ChangePswCommand(controller));
        commandRegistry.put("assign", new AssignCommand(controller));
        commandRegistry.put("setmax", new SetPersoneMaxCommand(controller));
        commandRegistry.put("exit", new ExitCommand(controller));
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
            if (!out)
                return out;
        }
        return out; // Ne basta una false per dare false come risultato
    }
}