package V4.Ingsoft.controller.commands;

import java.time.DayOfWeek;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.setup.CommandListSETUP;
import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class AssignCommand extends AbstractCommand {

    private final Controller controller;

    public AssignCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.ASSIGN; // CommandList.ASSIGN appena ho voglia di scriverlo
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'assign'");
            return;
        }

        String[] arg = StringUtils.joinQuotedArguments(args);

        if (arg.length < 2) {
            if ("V".equals(options[0])) {
                ViewSE.println("Errore nell'utilizzo del comando 'assign': assign -V NomeVisita UsernameVolontario");
            } else {
                ViewSE.println("Errore nell'utilizzo del comando 'assign': assign -L NomeLuogo NomeVisita");
            }
            return;
        }
        switch (options[0]) {
            case "V" -> {
                if (controller.canExecute16thAction)
                    assignVolontario(arg[0], arg[1]);
                else
                    ViewSE.println("Azione possibile solo il 16 del month!");
            }
            case "L" -> {
                if (controller.canExecute16thAction)
                    assignVisita(arg[0], arg[1]);
                else
                    ViewSE.println("Azione possibile solo il 16 del month!");
            }
            default ->
                ViewSE.println("Errore nell'utilizzo del comando 'assign'"); // Non può arrivare qua
        }
    }

    private void assignVolontario(String type, String userNameVolontario) {
        Volontario v = controller.db.findVolontario(userNameVolontario);
        TipoVisita vToAssign = controller.db.getTipoVisitaByName(type);

        if (v == null) {
            ViewSE.println("Nessun volontario trovato con quell'username.");
            return;
        }

        if (vToAssign == null) {
            ViewSE.println("Nessuna visita trovata con quel titolo.");
            return;
        }

        v.addTipoVisita(vToAssign.getUID());
        vToAssign.addVolontario(v.getUsername());
        ViewSE.println("Assegnato il volontario " + v.getUsername() + " alla visita " + vToAssign.getTitle());
    }

    private void assignVisita(String name, String type) {
        Luogo luogo = controller.db.getLuogoByName(name);
        TipoVisita visitaDaAssegnare = controller.db.getTipoVisitaByName(type);

        if (luogo == null) {
            ViewSE.println("Nessun luogo trovato con quel nome.");
            return;
        }
        if (visitaDaAssegnare == null) {
            ViewSE.println("Nessuna visita trovata con quel titolo.");
            return;
        }

        // Controlla, per ogni giorno in cui la visita può essere fatta, che non ci
        // siano conflitti
        boolean giorniPlausibili = true;
        for (DayOfWeek day : visitaDaAssegnare.getDays()) {
            if (!isAssignmentPlausibleOnDay(visitaDaAssegnare, luogo, day)) {
                giorniPlausibili = false;
                break;
            }
        }

        if (giorniPlausibili) {
            luogo.addTipoVisita(visitaDaAssegnare.getUID());
            visitaDaAssegnare.setLuogo(luogo.getUID());
            ViewSE.println("Assegnata la visita " + visitaDaAssegnare.getTitle() + " al luogo " + luogo.getName());
        } else {
            AssertionControl.logMessage("Non posso assegnare questa visita perchè si accavalla a qualche altra",
                    2, this.getClass().getSimpleName());
        }
    }

    /**
     * Verifica che la visita da assegnare non si sovrapponga ad altre già assegnate
     * al luogo
     * per il giorno specificato.
     */
    private boolean isAssignmentPlausibleOnDay(TipoVisita visitaDaAssegnare, Luogo luogo, DayOfWeek day) {
        for (String uidTipoVisita : luogo.getTipoVisitaUID()) {
            // Se è la stessa visita, la saltiamo
            if (uidTipoVisita.equals(visitaDaAssegnare.getUID()))
                continue;

            TipoVisita altraVisita = controller.db.getTipiByUID(uidTipoVisita);
            // Se l'altra visita non è programmata per questo giorno, prosegui
            if (!altraVisita.getDays().contains(day))
                continue;

            int startAltra = altraVisita.getInitTime().getMinutes();
            int startDaAssegnare = visitaDaAssegnare.getInitTime().getMinutes();

            // Controlla che gli orari non si accavallino.
            // Se non sono in conflitto, uno inizia dopo la fine dell'altro.
            if (!(startAltra > (startDaAssegnare + visitaDaAssegnare.getDuration())
                    || startDaAssegnare > (startAltra + altraVisita.getDuration()))) {
                AssertionControl.logMessage("Mi accavallo con " + altraVisita.getTitle(),
                        3, this.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }

}
