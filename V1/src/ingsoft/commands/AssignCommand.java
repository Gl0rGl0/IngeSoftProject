package ingsoft.commands;

import ingsoft.App;
import ingsoft.commands.setup.CommandListSETUP;
import ingsoft.persone.Volontario;
import ingsoft.util.Date;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;

public class AssignCommand extends AbstractCommand {

    private final App app;
    public AssignCommand(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.HELP; //CommandList.ASSIGNE appena ho voglia di scriverlo
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1 || options.length < 1) {
            ViewSE.print("Errore nell'utilizzo del comando 'assign'");
            return;
        }
        switch (options[0]) {
            case "v" -> assignVolontario(args);
            case "V" -> assignVisita(args);
            default -> ViewSE.print("Errore nell'utilizzo del comando 'assign'");
        }
    }

    private void assignVolontario(String[] args){
        String[] a = StringUtils.joinQuotedArguments(args);
        Volontario v = (Volontario) app.db.findUser(a[0]);      //try catch, se droppa errore non è un volontario -> oppure fare un findVolontario...
        //DA SCEGLIERE SE FARE UN HASHMAP<Volontario,Visita> o mettere il volontario dentro la visita. Non ho capito se una visita può essere fatta da più volontari scegli te
    }

    private void assignVisita(String[] args){
        //idem da scegliere
    }

    ::::ERRORE COSI MI APRI::::
}