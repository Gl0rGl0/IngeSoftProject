package V1.ingsoft.util;

import V1.ingsoft.App;

public class TimeHelper implements Runnable {

    private final App app;

    public TimeHelper(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        // Esempio: aggiorna la data simulata secondo la logica che preferisci
        app.date.incrementa();
    }
}
