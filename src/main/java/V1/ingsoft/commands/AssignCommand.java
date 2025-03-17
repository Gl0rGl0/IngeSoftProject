package V1.ingsoft.commands;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.setup.CommandListSETUP;
import V1.ingsoft.luoghi.Luogo;
import V1.ingsoft.luoghi.TipoVisita;
import V1.ingsoft.persone.Volontario;
import V1.ingsoft.util.StringUtils;

public class AssignCommand extends AbstractCommand {

    private final App app;

    public AssignCommand(App app) {
        this.app = app;
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
            if ("v".equals(options[0])) {
                ViewSE.println("Errore nell'utilizzo del comando 'assign': assign -v UsernameVolontario NomeVisita");
            } else {
                ViewSE.println("Errore nell'utilizzo del comando 'assign': assign -V NomeLuogo NomeVisita");
            }
            return;
        }
        switch (options[0]) {
            case "v" -> {
                if (app.date.getDay() == 16)
                    assignVolontario(arg[0], arg[1]);
                else
                    ViewSE.println("Azione possibile solo il 16 del month!");
            }
            case "t" -> {
                if (app.date.getDay() == 16)
                    assignVisita(arg[0], arg[1]);
                else
                    ViewSE.println("Azione possibile solo il 16 del month!");
            }
            default ->
                ViewSE.println("Errore nell'utilizzo del comando 'assign'"); // Non può arrivare qua
        }
    }

    private void assignVolontario(String userNameVolontario, String type) {
        Volontario v = app.db.findVolontario(userNameVolontario);
        TipoVisita vToAssign = app.db.getTipoVisitaByName(type);

        if (v == null) {
            ViewSE.println("Nessun volontario trovato con quell'username.");
            return;
        }

        if (vToAssign == null) {
            ViewSE.println("Nessuna visita trovata con quel titolo.");
            return;
        }
        v.addTipoVisita(vToAssign.getUID());
        ViewSE.println("Assegnato il volontario " + v.getUsername() + " alla visita " + vToAssign.getTitle());
    }

    private void assignVisita(String name, String type) {
        Luogo l = app.db.getLuogoByName(name);
        TipoVisita vToAssign = app.db.getTipoVisitaByName(type);

        if (l == null) {
            ViewSE.println("Nessuna luogo trovato con quel nome.");
            return;
        }

        if (vToAssign == null) {
            ViewSE.println("Nessuna visita trovata con quel titolo.");
            return;
        }

        l.addTipoVisita(vToAssign.getUID());
        ViewSE.println("Assegnata la visita " + vToAssign.getTitle() + " al luogo " + l.getName());
    }
}
