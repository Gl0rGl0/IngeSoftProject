package ingsoft.commands;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.setup.CommandListSETUP;

public class GestioneRaccolta extends AbstractCommand {

    private final App app;
    boolean raccoltaAperta;

    public GestioneRaccolta(App app) {
        this.app = app;
        raccoltaAperta = app.date.getGiorno() != 16;
        super.commandInfo = CommandListSETUP.ASSIGN; // CommandList.ASSIGN appena ho voglia di scriverlo
    }

    @Override
    public void execute(String[] options, String[] args) {
        if(app.date.getGiorno() != 16) {
            ViewSE.println("Azione possibile solo il 16 del mese!");
            return;
        }

        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'presence'");
            return;
        }

        switch (options[0]) {
            // case "o" -> {if(!app.raccoltaAperta) app.raccoltaAperta = true; else ViewSE.println("La raccolta delle disponibilità è già aperta");}
            // case "c" -> {if(app.raccoltaAperta) app.raccoltaAperta = false; else ViewSE.println("La raccolta delle disponibilità è già chiusa");}
            default -> 
                ViewSE.println("Errore nell'utilizzo del comando 'presence'");
        }
    }
}
