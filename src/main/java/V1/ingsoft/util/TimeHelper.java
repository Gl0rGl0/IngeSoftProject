package V1.ingsoft.util;

import V1.ingsoft.controller.Controller;

public class TimeHelper implements Runnable {

    private final Controller app;

    public TimeHelper(Controller app) {
        this.app = app;
    }

    @Override
    public void run() {
        // Esempio: aggiorna la data simulata secondo la logica che preferisci
        app.date.incrementa();
    }
}
