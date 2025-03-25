package V4.Ingsoft.util;

import V4.Ingsoft.controller.Controller;

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
