package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.luoghi.StatusVisita;
import ingsoft.luoghi.TipoVisita;
import ingsoft.persone.Volontario;
import ingsoft.util.ViewSE;

public class ListCommand extends AbstractCommand {

    private final App app;

    public ListCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.LIST;
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
            ViewSE.print("Errore nell'utilizzo del comando 'list'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'v' -> listVolontari();
            case 'L' -> listLuoghi();
            case 'l' -> listLuoghi();
            case 't' -> printTipi(options);
            default -> ViewSE.print("Opzione non riconosciuta per 'list'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void listLuoghi(){
        //for (Luogo l : app.db.getLuoghi()) {
        //    ViewSE.print(l);
        //}
        app.db.getLuoghi().forEach(l -> ViewSE.print(l)); //boh sono uguali
    }

    private void listVolontari(){
        for (Volontario v : app.db.getVolontari()) {
            ViewSE.print(v);    //DA AGGIUNGERE LE VISITE A ESSO COLLEGHATE app.db.trovaVisite(volontario)?
        }
    }

    private void printTipi(String[] s){
        char option = 'a';
        try {
            option = s[1].charAt(0);
        } catch (Exception e) {
            //
        }
        
        switch (option) {
            case 'a' -> printAllTipi();
            case 'p' -> printProposte();
            case 'c' -> printComplete();
            case 'C' -> printCancellate();
            case 'e' -> printEffettuate();
            default -> ViewSE.print("Opzione non riconosciuta per 'list'.");
        }
    }

    // [-a]  All, tutte le visite (anche passate)       le faccio passare tutte
    // [-p]  Lista delle visite Proposte                Guardo nelle visite attuali quelle con stato PROPOSTE
    // [-c]  Lista delle visite Complete                Guardo nelle visite attuali quelle con stato COMPLETE
    // [-C]  Lista delle visite Cancellate              Guardo nelle visite attuali quelle con stato CANCELLATE
    // [-e]  Lista delle visite Effettuate (passate)    Prendo l'array della cronologia e le filtro con effettuate -> prendo dall'archivio (archivio.txt?)
    
    private void listTipi() {
            for (TipoVisita v : this.app.db.getTipi()) {
            ViewSE.print(v);
        }
    }

    private void printAllTipi() {
        //tutti i print sotto
    }

    private void printProposte() {
        for (TipoVisita v : app.db.getVisite()) {
            ViewSE.print(v);
        }
    }

    private void printComplete() {
        for (Volontario v : app.db.getVolontari()) {
            for(TipoVisita V : v.getVisite(StatusVisita.COMPLETA)){
                ViewSE.print(V);
            }
        }
    }

    private void printCancellate() {
        for (Volontario v : app.db.getVolontari()) {
            for(TipoVisita V : v.getVisite(StatusVisita.CANCELLATA)){
                ViewSE.print(V);
            }
        }
    }

    private void printEffettuate() {
        ViewSE.print("NON ACNORA SUPPORTATO");
    }
}
