package V5.ingsoft;

import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests for Use Cases UC4-UC9 (Setup Phase)
public class SetupPhaseTests extends BaseTest {


    // Helper method to perform the initial login and mandatory password change
    // to enter the setup phase for most tests.
    private void enterSetupPhase() {
        // First absolute login with default credentials
        controller.interpreter("login ADMIN PASSWORD");
        assertEquals(PersonaType.CONFIGURATORE.toString(), controller.getCurrentUser().getType().toString());
    }

    // --- Setup Phase Tests ---

    // UC5 - Assegnazione Ambito Territoriale (Setup)
    @Test
    public void testSetupSetAmbitoSuccess() {
        // Arrange
        enterSetupPhase();
    }

    // // UC6 - Assegnazione Numero Massimo Persone per Iscrizione (Setup)
    @Test
    public void testSetupSetPersoneMaxSuccess() {
        // Arrange
        enterSetupPhase();

        // Act
        controller.interpreter("setmax 5"); // Correct setup command

        // Assert
        assertEquals(5, Model.appSettings.getMaxPrenotazioniPerPersona(), "Max persone should be set to 5.");
    }

    @Test
    public void testSetupSetPersoneMaxFailZero() {
        // Arrange
        enterSetupPhase();

        // Act
        controller.interpreter("setmax 0"); // Correct setup command

        // Assert
        // Default in AppSettings is 1, and setMax ensures it's at least 1.
        assertEquals(1, Model.appSettings.getMaxPrenotazioniPerPersona(), "Max persone should be 1 after attempting to set 0.");
    }

    @Test
    public void testSetupSetPersoneMaxFailNegative() {
        // Arrange
        enterSetupPhase();

        // Act
        controller.interpreter("setmax -1"); // Correct setup command

        // Assert
        // Assuming negative is invalid, should remain default (1).
        // Let's assume default 1 for this test.
        assertEquals(1, Model.appSettings.getMaxPrenotazioniPerPersona(), "Max persone should remain 1 after attempting to set negative value.");
    }

    @Test
    public void testSetupSetPersoneMaxFailNotANumber() {
        // Arrange
        enterSetupPhase();

        // Act
        controller.interpreter("setmax five"); // Correct setup command

        // Assert
        // Assuming non-number is invalid, should remain default (1) or previous value (5).
        // Let's assume default 1 for this test.
        assertEquals(1, Model.appSettings.getMaxPrenotazioniPerPersona(), "Max persone should remain 1 after attempting to set non-number.");
    }

    // UC7 - Aggiunta Luogo (Setup)
    @Test
    public void testSetupAddLuogoSuccessDescription() {
        // Arrange
        enterSetupPhase();

        // Act
        controller.interpreter("add -L TestPlace \"Test Description\" 10.1,20.2"); // Correct setup command

        // Assert
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("TestPlace"), "TestPlace should exist after adding.");
        assertEquals("Test Description", controller.getDB().dbLuoghiHelper.findLuogo("TestPlace").getDescription());
        // Could also check coordinates if GPS class has getters/equals
    }

    @Test
    public void testSetupAddLuogoSuccessAddress() {
        // Arrange
        enterSetupPhase();

        // Act
        controller.interpreter("add -L Place2 \"Address based location\" \"123 Main St, City\""); // Correct setup command (assuming GPS string can be address)

        // Assert
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("Place2"), "Place2 should exist after adding with address.");
        assertEquals("Address based location", controller.getDB().dbLuoghiHelper.findLuogo("Place2").getDescription());
        // Could check GPS object if it stores the address string
    }

    @Test
    public void testSetupAddLuogoFailDuplicateTitle() {
        // Arrange
        enterSetupPhase();
        controller.interpreter("add -L TestPlace \"First Place\" 10.1,20.2");

        // Act
        controller.interpreter("add -L TestPlace \"Second Place\" 30.3,40.4");

        // Assert
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("TestPlace"), "TestPlace should still exist.");
        assertEquals("First Place", controller.getDB().dbLuoghiHelper.findLuogo("TestPlace").getDescription(), "Place1 description should not change on duplicate add attempt.");
    }

    @Test
    public void testSetupAddLuogoFailEmptyTitle() {
        // Arrange
        enterSetupPhase();

        // Act
        controller.interpreter("add -L \"\" \"Empty Title\" 10.1,20.2"); // Correct setup command

        // Assert
        // Assuming empty title is invalid
        assertNull(controller.getDB().dbLuoghiHelper.findLuogo(""), "Place with empty title should not be added.");
    }

    // UC9 - Associazione TipoVisita-Luogo (Setup) is implicitly tested via 'add tipovisita' tests.
    // Since 'add tipovisita' doesn't exist in setup, this UC might be handled differently.

    // UC4 - Creazione Corpo Dati Iniziale (Setup) - 'done' command tests
    @Test
    public void testSetupDoneSuccess() {
        // Arrange: Complete all required setup steps
        enterSetupPhase();
        controller.interpreter("setmax 5");
        controller.interpreter("add -L Place1 \"Desc\" 10.0,20.0");

        // Act
        controller.interpreter("done"); // Correct setup command

        // Assert
        assertTrue(controller.setupCompleted(), "Setup should be marked as complete after 'done'.");
    }

    @Test
    public void testSetupDoneFailMissingPersoneMax() {
        // Arrange: Miss setmax
        enterSetupPhase();
        controller.interpreter("add -L Place1 \"Desc\" 10.0,20.0");

        // Act
        controller.interpreter("done"); // Correct setup command

        // Assert
        assertFalse(controller.setupCompleted(), "Setup should not be complete if personeMax is missing.");
        // Verify personeMax can still be set
        controller.interpreter("setmax 10");
        assertEquals(10, Model.appSettings.getMaxPrenotazioniPerPersona(), "Should be able to set personeMax if 'done' failed.");
    }

    @Test
    public void testSetupDoneFailMissingLuogo() {
        // Arrange: Miss adding luogo
        enterSetupPhase();
        controller.interpreter("setmax 5");

        // Act
        controller.interpreter("done"); // Correct setup command

        // Assert
        assertFalse(controller.setupCompleted(), "Setup should not be complete if luogo is missing.");
        // Verify luogo can still be added
        controller.interpreter("add -L PlaceAfterFail \"Added after fail\" 1.1:2.2");
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceAfterFail"), "Should be able to add luogo if 'done' failed.");
    }

}
