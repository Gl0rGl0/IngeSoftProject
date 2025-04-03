// package V4.ingsoft;

// import static org.junit.jupiter.api.Assertions.assertEquals; // Import assertEquals
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import V4.Ingsoft.controller.Controller;
// import V4.Ingsoft.controller.item.persone.PersonaType;
// import V4.Ingsoft.model.Model;
// import V4.Ingsoft.util.Initer;
// import V4.Ingsoft.view.ViewSE;

// public class AppTest {
//     public Controller controller;
//     public Model model;
//     public ViewSE view;

//     public static void test(Controller controller) {
//         controller.skipSetupTesting = true;
//         controller.interpreterSETUP("login ADMIN PASSWORD");

//         Initer.initPersone(controller);
//         // Initer.initVisiteLuoghi(controller);
//         // Initer.initAvailability(controller);

//         controller.interpreterSETUP("logout");
//     }

//     @BeforeEach
//     public void inizializzaTutto() {
//         model = new Model();
//         controller = new Controller(model);
//         view = new ViewSE(controller);

//         test(controller);
//     }

//     @Test
//     public void loginConfiguratore() {
//         controller.interpreter("login config1 pass1C");
//         assertEquals(PersonaType.CONFIGURATORE, controller.user.getType(), "User should be CONFIGURATORE after login.");
//     }

//     @Test
//     public void loginVolontario() {
//         controller.interpreter("login volont1 pass1V");
//         assertEquals(PersonaType.VOLONTARIO, controller.user.getType(), "User should be VOLONTARIO after login.");
//     }

//     @Test
//     public void loginFruitore() {
//         controller.interpreter("login fruit1 pass1F");
//         assertEquals(PersonaType.FRUITORE, controller.user.getType(), "User should be FRUITORE after login.");
//     }
// }
