package V2.Ingsoft.util;

import V2.Ingsoft.controller.Controller;

public class TimeHelper implements Runnable {

    private final Controller controller;

    public TimeHelper(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        // Example: update the simulated date according to your preferred logic
        controller.date.incrementa();
    }
}
