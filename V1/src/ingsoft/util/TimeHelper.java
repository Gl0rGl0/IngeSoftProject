package ingsoft.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ingsoft.App;

public class TimeHelper implements Runnable {

    private final App app; // o la classe che contiene il tempo simulato

    public TimeHelper(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        // Esempio: aggiorna la data simulata secondo la logica che preferisci
        app.date.incrementa(); // implementa il metodo incrementa() come preferisci

        // Controlla se è il 16 del mese
        if(app.date.getGiorno() == 16) {
            // Esegui le azioni programmate per il 16 del mese
            eseguiAzioniDel16();
        }
    }

    private void eseguiAzioniDel16() {
        // Logica per le azioni da eseguire
        System.out.println("Esecuzione azioni del 16 del mese!");
    }
}
