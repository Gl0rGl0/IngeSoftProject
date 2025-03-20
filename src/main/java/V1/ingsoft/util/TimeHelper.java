package V1.ingsoft.util;

import V1.ingsoft.controller.Controller;

public class TimeHelper implements Runnable {

    private final Controller controller;

    public TimeHelper(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        // Esempio: aggiorna la data simulata secondo la logica che preferisci
        controller.date.incrementa();
    }
}
