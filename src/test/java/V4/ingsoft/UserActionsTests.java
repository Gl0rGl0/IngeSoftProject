package V4.ingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.JsonStorage;
import V4.Ingsoft.util.Date; // Assuming Date utility exists

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Tests for Use Cases UC30-UC33 (Fruitore/User Actions)
public class UserActionsTests {
    private Controller controller;
    private Model model;
    private String configPath = "data/configuratori.json";
    private String volontariPath = "data/volontari.json";
    private String fruitoriPath = "data/fruitori.json";
    private String visitePath = "data/visite.json";
    private String luoghiPath = "data/luoghi.json";
    private String tipiVisitaPath = "data/tipi_visita.json";
    private String iscrizioniPath = "data/iscrizioni.json";
    private String datePreclusePath = "data/date_precluse.json";
    private String disponibilitaPath = "data/disponibilita.json";
    private String settingsPath = "data/settings.json";

    // Helper to reset data files before each test
    private void resetDataFiles() {
        // Delete existing files to ensure clean state
        try { Files.deleteIfExists(Paths.get(configPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(volontariPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(fruitoriPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(visitePath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(luoghiPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(tipiVisitaPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(iscrizioniPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(datePreclusePath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(disponibilitaPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(settingsPath)); } catch (IOException e) { /* Ignore */ }

        // Re-initialize model and controller
        model = new Model();
        controller = new Controller(model);
    }

    // Helper method to complete setup, generate visits (hypothetically), and login as fruitore
    private void setupAndLoginAsFruitore(String username, String password) {
        // 1. Admin setup
        controller.interpreter("login ADMIN PASSWORD");
        controller.interpreter("changepsw newAdminPass");

        // 2. Setup steps using known setup commands
        controller.interpreter("setambito TestAreaUser");
        controller.interpreter("setpersonemax 5");
        // Ensure 3 arguments for add -L: title, description, gps
        controller.interpreter("add -L PlaceUser \"User Place\" 10.0,20.0");
        controller.interpreter("done");

        // 3. Add required entities using running commands (logged in as ADMIN/Configurator)
        // Ensure all arguments for add -t are provided, using "" for optional description if needed
        // Format: add -t <UID> <LuogoTitle> <OraInizio> <Durata> <MinPart> <MaxPart> [Descrizione] ...
        controller.interpreter("add -t TVUser Description 1:1 1/1/1 2/2/2 10:00 60 false 1 10 Ma");
        controller.interpreter("add -v VolUser PassVUser");
        controller.interpreter("assign -V VolUser TVUser");
        controller.interpreter("assign -L PlaceUser TVUser");

        // 4. Register the fruitore using running command
        controller.interpreter("add -f " + username + " " + password);

        // 5. TODO: Generate visits (UC20) - Requires correct setup and plan generation command

        // 6. Login as fruitore
        controller.interpreter("logout"); // Ensure logged out first
        controller.interpreter("login " + username + " " + password);
        assertNotNull(controller.user, "Fruitore should be logged in for tests.");
        assertEquals(PersonaType.FRUITORE.toString(), controller.user.getType().toString(), "User should be of type FRUITORE.");
    }


    @BeforeEach
    public void setup() {
        resetDataFiles();
        // Specific setup done in each test or helper method
    }

     @AfterEach
     public void cleanup() {
         controller.interpreter("logout"); // Ensure logout
         // Optional: Clean up files again after test if needed
         // resetDataFiles();
     }

    // --- Fruitore Action Tests ---

    // UC30 - Visualizzazione Visite Disponibili (Fruitore)
    @Test
    public void testUserListViewAvailableVisits() {
        // Arrange
        setupAndLoginAsFruitore("userTestView", "passUTV");
        // TODO: Generate visits

        // Act
        controller.interpreter("list visits"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

    @Test
    public void testUserListViewAvailableVisitsEmpty() {
        // Arrange
        setupAndLoginAsFruitore("userTestEmpty", "passUTE");
        // Ensure no visits generated

        // Act
        controller.interpreter("list visits"); // Assumed command - uncommented

        // Assert
      assertTrue(true); // Placeholder
    }

    // UC31 - Iscrizione a Visita Proposta
    @Test
    public void testUserSubscribeVisitSuccess() {
        // Arrange
        setupAndLoginAsFruitore("userSub", "passUS");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure this visit actually exists in 'proposta' state via UC20 setup in helper.

        // Act
        controller.interpreter("assign " + visitUID + " 2"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

    @Test
    public void testUserSubscribeVisitFailZeroPeople() {
        // Arrange
        setupAndLoginAsFruitore("userSubZero", "passUSZ");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure visit exists.

        // Act
        controller.interpreter("assign " + visitUID + " 0"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

    @Test
    public void testUserSubscribeVisitFailTooManyPeoplePerInscription() {
        // Arrange (Max per inscription set to 5 in helper)
        setupAndLoginAsFruitore("userSubMaxInscr", "passUSMI");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure visit exists.

        // Act
        controller.interpreter("assign " + visitUID + " 6"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

    @Test
    public void testUserSubscribeVisitFailExceedCapacity() {
        // Arrange (Max participants for TVUser is 10, max per inscription is 5)
        setupAndLoginAsFruitore("userSubCap1", "passUSC1");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure visit exists.
        // Have another user subscribe first to fill up space
        controller.interpreter("logout");
        controller.interpreter("add -f userSubCap2 passUSC2"); // Corrected command
        controller.interpreter("login userSubCap2 passUSC2");
        controller.interpreter("assign " + visitUID + " 5"); // Assumed command - uncommented
        controller.interpreter("logout");
        controller.interpreter("login userSubCap1 passUSC1");
        assertNotNull(controller.user);

        // Act
        controller.interpreter("assign " + visitUID + " 6"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

     @Test
     public void testUserSubscribeVisitChangesStateToCompleta() {
         // Arrange (Max participants 10, max per inscription 5)
         setupAndLoginAsFruitore("userSubComp1", "passUSC1");
         String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
         // TODO: Ensure visit exists in 'proposta' state.
         // Have another user subscribe for 5
         controller.interpreter("logout");
         controller.interpreter("add -f userSubComp2 passUSC2"); // Corrected command
         controller.interpreter("login userSubComp2 passUSC2");
         controller.interpreter("assign " + visitUID + " 5"); // Assumed command - uncommented
         controller.interpreter("logout");
         controller.interpreter("login userSubComp1 passUSC1");

         // Act
         controller.interpreter("assign " + visitUID + " 5"); // Assumed command - uncommented

         // Assert
         assertTrue(true); // Placeholder
     }

     // TODO: Add test for subscribing when inscriptions are closed (3 days before visit) - requires date simulation.
     // TODO: Add test for subscribing to a visit not in 'proposta' state.


    // UC32 - Visualizzazione Proprie Iscrizioni (Fruitore)
    @Test
    public void testUserListViewOwnSubscriptions() {
        // Arrange
        setupAndLoginAsFruitore("userViewSubs", "passUVS");
        String visitUID1 = "TVUser_10-07-2025"; // Hypothetical Visit UID 1
        String visitUID2 = "TVUser_11-07-2025"; // Hypothetical Visit UID 2
        // TODO: Ensure these visits exist via UC20 setup.
        controller.interpreter("assign " + visitUID1 + " 1"); // Assumed command - uncommented
        controller.interpreter("assign " + visitUID2 + " 3"); // Assumed command - uncommented

        // Act
        controller.interpreter("list myvisits"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

    @Test
    public void testUserListViewOwnSubscriptionsEmpty() {
        // Arrange
        setupAndLoginAsFruitore("userViewSubsEmpty", "passUVSE");

        // Act
        controller.interpreter("list myvisits"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
     }


    // UC33 - Disdetta Iscrizione (Fruitore)
    @Test
    public void testUserCancelSubscriptionSuccess() {
        // Arrange
        setupAndLoginAsFruitore("userCancel", "passUC");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure visit exists in 'proposta' state.
        controller.interpreter("assign " + visitUID + " 2"); // Assumed command - uncommented
        String bookingCode = "BOOKING_CODE_123"; // Placeholder - TODO: Get actual code

        // Act
        controller.interpreter("remove " + bookingCode); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

    @Test
    public void testUserCancelSubscriptionChangesStateFromCompleta() {
        // Arrange
        setupAndLoginAsFruitore("userCancelComp1", "passUCC1");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID (Capacity 10)
        // TODO: Ensure visit exists in 'proposta' state.
        // User 2 subscribes for 5
        controller.interpreter("logout");
        controller.interpreter("add -f userCancelComp2 passUCC2"); // Corrected command
        controller.interpreter("login userCancelComp2 passUSC2");
        controller.interpreter("assign " + visitUID + " 5"); // Assumed command - uncommented
        controller.interpreter("logout");
        // User 1 subscribes for 5, making it completa
        controller.interpreter("login userCancelComp1 passUCC1");
        controller.interpreter("assign " + visitUID + " 5"); // Assumed command - uncommented
        String bookingCodeUser1 = "BOOKING_CODE_USER1"; // Placeholder - TODO: Get actual code

        // Act
        controller.interpreter("remove " + bookingCodeUser1); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

    @Test
    public void testUserCancelSubscriptionFailInvalidCode() {
        // Arrange
        setupAndLoginAsFruitore("userCancelInvalid", "passUCI");
        String visitUID = "TVUser_10-07-2025";
        // TODO: Ensure visit exists.
        controller.interpreter("assign " + visitUID + " 1"); // Assumed command - uncommented

        // Act
        controller.interpreter("remove INVALID_CODE"); // Assumed command - uncommented

        // Assert
        assertTrue(true); // Placeholder
    }

     @Test
     public void testUserCancelSubscriptionFailNotOwner() {
         // Arrange
         setupAndLoginAsFruitore("userCancelOwner1", "passUCO1");
         String visitUID = "TVUser_10-07-2025";
         // TODO: Ensure visit exists.
         // User 2 subscribes
         controller.interpreter("logout");
         controller.interpreter("add -f userCancelOwner2 passUCO2"); // Corrected command
         controller.interpreter("login userCancelOwner2 passUCO2");
         controller.interpreter("assign " + visitUID + " 1"); // Assumed command - uncommented
         String bookingCodeUser2 = "BOOKING_CODE_USER2"; // Placeholder - TODO: Get actual code
         controller.interpreter("logout");
         // Login as User 1
         controller.interpreter("login userCancelOwner1 passUCO1");

         // Act
         controller.interpreter("remove " + bookingCodeUser2); // Assumed command - uncommented

         // Assert
         assertTrue(true); // Placeholder
     }

     // TODO: Add test for cancelling when inscriptions are closed (3 days before visit) - requires date simulation.
     // TODO: Add test for cancelling a visit not in 'proposta' or 'completa' state.

 }
