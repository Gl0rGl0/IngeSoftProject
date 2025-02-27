package ingsoft.util;

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
        

            // if(il16epassato && !eseguita && !app.date.festivo()){
            //     eseguiAzioniDel16();
            //     il16epassato = false;
            // }    //Questa roba è da mettere in App
    }
}
