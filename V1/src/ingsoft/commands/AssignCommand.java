package ingsoft.commands;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.setup.CommandListSETUP;
import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.TipoVisita;
import ingsoft.persone.Volontario;
import ingsoft.util.StringUtils;

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
            case "v" ->
                assignVolontario(arg[0], arg[1]);
            case "t" ->
                assignVisita(arg[0], arg[1]);
            default ->
                ViewSE.println("Errore nell'utilizzo del comando 'assign'"); // Non può arrivare qua
        }
    }

    private void assignVolontario(String usernameVolontario, String nomeVisita) {
        Volontario v = app.db.findVolontario(usernameVolontario);
        TipoVisita vToAssign = app.db.getTipoVisitaByName(nomeVisita);

        if (v == null) {
            ViewSE.println("Nessun volontario trovato con quell'username.");
            return;
        }

        if (vToAssign == null) {
            ViewSE.println("Nessuna visita trovata con quel titolo.");
            return;
        }
        v.aggiungiTipoVisita(vToAssign.getUID());
        ViewSE.println("Assegnato il volontario " + v.getUsername() + " alla visita " + vToAssign.getTitolo());
    }

    private void assignVisita(String nomeLuogo, String nomeVisita) {
        Luogo l = app.db.getLuogoByName(nomeLuogo);
        TipoVisita vToAssign = app.db.getTipoVisitaByName(nomeVisita);

        if (l == null) {
            ViewSE.println("Nessuna luogo trovato con quel nome.");
            return;
        }

        if (vToAssign == null) {
            ViewSE.println("Nessuna visita trovata con quel titolo.");
            return;
        }

        l.aggiungiTipoVisita(vToAssign.getUID());
        ViewSE.println("Assegnata la visita " + vToAssign.getTitolo() + " al luogo " + l.getNome());
    }
}
