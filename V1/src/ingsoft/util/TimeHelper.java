package ingsoft.util;

import ingsoft.App;

public class TimeHelper implements Runnable {

    private final App app; // o la classe che contiene il tempo simulato
    private boolean eseguita = false;
    private boolean il16epassato = false;

    public TimeHelper(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        // Esempio: aggiorna la data simulata secondo la logica che preferisci
        app.date.incrementa(); // implementa il metodo incrementa() come preferisci
        


        // Controlla se è il 16 del mese
        if(app.date.getGiorno() == 16 && !app.date.festivo()) {
            il16epassato = true;
            if(!eseguita){
                eseguiAzioniDel16();
            }
        }else{
            eseguita = false;
            
            if(il16epassato && !eseguita && !app.date.festivo()){
                eseguiAzioniDel16();
                il16epassato = false;
            }
        }
    }

    private void eseguiAzioniDel16() {
        app.azioneDelGiorno(); // ?
        eseguita = true;
        ViewSE.println("Esecuzione azioni del 16 del mese!");
    }
}
