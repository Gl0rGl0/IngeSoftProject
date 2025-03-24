package V1.ingsoft.controller.commands;

import java.time.DayOfWeek;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.setup.CommandListSETUP;
import V1.ingsoft.controller.item.luoghi.Luogo;
import V1.ingsoft.controller.item.luoghi.TipoVisita;
import V1.ingsoft.controller.item.persone.Volontario;
import V1.ingsoft.util.AssertionControl;
import V1.ingsoft.util.StringUtils;
import V1.ingsoft.view.ViewSE;

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
        Luogo l = controller.db.getLuogoByName(name);
        TipoVisita vToAssign = controller.db.getTipoVisitaByName(type);

        if (l == null) {
            ViewSE.println("Nessun luogo trovato con quel nome.");
            return;
        }

        if (vToAssign == null) {
            ViewSE.println("Nessuna visita trovata con quel titolo.");
            return;
        }

        boolean dayPlausibles = true;
        for (DayOfWeek d : DayOfWeek.values()) {

            if (!vToAssign.getDays().contains(d))
                continue;

            for (String uidTipoVisita : l.getTipoVisitaUID()) {
                if (uidTipoVisita.equals(vToAssign.getUID()))
                    continue;

                TipoVisita toOperate = controller.db.getTipiByUID(uidTipoVisita);

                if (!toOperate.getDays().contains(d))
                    continue;

                int oraInizioT1 = toOperate.getInitTime().getMinutes();
                int oraInizioT2 = vToAssign.getInitTime().getMinutes();

                if (oraInizioT1 > (oraInizioT2 + vToAssign.getDuration())
                        || oraInizioT2 > (oraInizioT1 + toOperate.getDuration())) {
                } else {
                    AssertionControl.logMessage("Mi accavallo con " + toOperate.getTitle(), 3,
                            this.getClass().getSimpleName());
                    dayPlausibles = false;
                }

                if (!dayPlausibles)
                    break;
            }

            // if (!dayPlausibles) {
            // // vToAssign. TODO Se una visita non può essere un giorno bisogna non
            // assegnarla
            // // o togliere quel giorno dalle possibilità
            // }
        }

        if (dayPlausibles) {
            l.addTipoVisita(vToAssign.getUID());
            vToAssign.setLuogo(l.getUID());
            ViewSE.println("Assegnata la visita " + vToAssign.getTitle() + " al luogo " + l.getName());
        } else {
            AssertionControl.logMessage("Non posso assegnare questa visita perchè si accavalla a qualche altra", 2,
                    this.getClass().getSimpleName());
        }
    }
}
