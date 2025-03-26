package V4.ingsoft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.Initer;
import V4.Ingsoft.view.ViewSE;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    public Controller controller;
    public Model model;
    public ViewSE view;

    public static void test(Controller controller) {
        controller.skipSetupTesting = true;
        controller.interpreter("login ADMIN PASSWORD");

        Initer.initPersone(controller);
        Initer.initVisiteLuoghi(controller);
        Initer.initAvailability(controller);

        controller.interpreter("logout");
    }

    @BeforeEach
    public void inizializzaTutto() {
        model = new Model();
        controller = new Controller(model);
        view = new ViewSE(controller);

        controller.dailyAction();

        test(controller);

        controller.interpreter("login config1 pass1C");
        controller.interpreter("changepsw pass1C pass1C");
    }

    @Test
    public void visualizzaVisiteFruitore(){
        controller.interpreter("logout");
        assert(controller.user.getType()==PersonaType.GUEST);
        controller.interpreter("login fruit1 pass1F");
        assert(controller.user.getType()==PersonaType.FRUITORE);
    }
}