package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.Date;
import ingsoft.util.ViewSE;

public class PrecludeCommand extends AbstractCommand{
    
    private final App app;
    public PrecludeCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.PRECLUDE;
    }

    @Override
    public void execute(String[] options, String[] args) {
        
        if (options.length < 1) {
            ViewSE.print("Errore nell'utilizzo del comando 'preclude'");
            return;
        }

        Date toOperate = new Date(args[0]);     //SPERANDO CHE SIA NEL FORMATO CORRETTO, MAGARI UN TRYCATCH PROSSIMAMENTE

        if(separatiDaDueMesi(toOperate, app.date)){
            ViewSE.print("Non è possibile aggiungere una data cosi avanti/indietro nel tempo, attenersi al mese successivo al prossimo");
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'a' -> addPrecludedDate(args);
            case 'r' -> removePrecludedDate(args);
            default -> ViewSE.print("Opzione non riconosciuta per 'preclude'.");
        }
    }

    private boolean separatiDaDueMesi(Date d1, Date d2) {
        // d2: data corrente (app.date) (mese e giorno 1-based)
        int currentMonth = d2.getMese();
        int targetMonth = d1.getMese();
        int T = d1.getGiorno() < 16 ? 0 : 1;

        if(T == 1){
            return (currentMonth + 1 + T) == targetMonth;
        }else{
            return (currentMonth + 2) == targetMonth;
        }

        //APRI Spiegazione.png <--- o scrivimi che cazzo ne so
    }    
    

    private void addPrecludedDate(String[] args) {
        ViewSE.print("Eseguo: Aggiungo data da precludere");
        app.db.addPrecludedDate(new Date(args[0]));  //aggiunge una data speciale
    }

    private void removePrecludedDate(String[] args) {
        ViewSE.print("Eseguo: Rimuovo data da precludere");
        app.db.removePrecludedDate(new Date(args[0]));
    }
}
