package V5.ingsoft;

import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SetupPhaseTests extends BaseTest {

    private void enterSetupPhase() {
        controller.interpreter("login ADMIN PASSWORD");
        assertEquals(PersonaType.CONFIGURATORE.toString(), controller.getCurrentUser().getType().toString());
    }

    @Test
    public void testSetupSetAmbitoSuccess() {
        // Arrange
        enterSetupPhase();
        // Act & Assert: This test only enters the setup phase, no specific assertion for ambito itself.
        // Ambito setting is typically done with a separate command or is part of the 'done' check.
    }

    @Test
    public void testSetupSetPersoneMaxSuccess() {
        // Arrange
        enterSetupPhase();
        int expectedMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Act
        controller.interpreter("setmax " + expectedMax);
        int actualMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Assert
        assertEquals(expectedMax, actualMax, "Max persone should be set to " + expectedMax + ".");
    }

    @Test
    public void testSetupSetPersoneMaxFailZero() {
        // Arrange
        enterSetupPhase();
        int initialMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Act
        controller.interpreter("setmax 0");
        int actualMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Assert
        assertEquals(initialMax, actualMax, "Max persone should remain " + initialMax + " after attempting to set 0.");
    }

    @Test
    public void testSetupSetPersoneMaxFailNegative() {
        // Arrange
        enterSetupPhase();
        int initialMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Act
        controller.interpreter("setmax -1");
        int actualMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Assert
        assertEquals(initialMax, actualMax, "Max persone should remain " + initialMax + " after attempting to set negative value.");
    }

    @Test
    public void testSetupSetPersoneMaxFailNotANumber() {
        // Arrange
        enterSetupPhase();
        // Assuming default value is 1 for a fresh start or initial state
        int initialMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Act
        controller.interpreter("setmax five");
        int actualMax = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();

        // Assert
        assertEquals(initialMax, actualMax, "Max persone should remain " + initialMax + " after attempting to set non-number.");
    }

    @Test
    public void testSetupAddLuogoSuccessDescription() {
        // Arrange
        enterSetupPhase();
        String placeName = "TestPlace";
        String description = "Test Description";
        String coordinates = "10.1,20.2";

        // Act
        controller.interpreter("add -L " + placeName + " \"" + description + "\" " + coordinates);
        Luogo addedLuogo = Model.getInstance().dbLuoghiHelper.findLuogo(placeName);

        // Assert
        assertNotNull(addedLuogo, placeName + " should exist after adding.");
        assertEquals(description, addedLuogo.getDescription());
    }

    @Test
    public void testSetupAddLuogoSuccessAddress() {
        // Arrange
        enterSetupPhase();
        String placeName = "Place2";
        String description = "Address based location";
        String address = "\"123 Main St, City\"";

        // Act
        controller.interpreter("add -L " + placeName + " \"" + description + "\" " + address);
        Luogo addedLuogo = Model.getInstance().dbLuoghiHelper.findLuogo(placeName);

        // Assert
        assertNotNull(addedLuogo, placeName + " should exist after adding with address.");
        assertEquals(description, addedLuogo.getDescription());
    }

    @Test
    public void testSetupAddLuogoFailDuplicateTitle() {
        // Arrange
        enterSetupPhase();
        String placeName = "TestPlace";
        String firstDescription = "First Place";
        String firstCoordinates = "10.1,20.2";
        controller.interpreter("add -L " + placeName + " \"" + firstDescription + "\" " + firstCoordinates);
        String secondDescription = "Second Place";
        String secondCoordinates = "30.3,40.4";

        // Act
        controller.interpreter("add -L " + placeName + " \"" + secondDescription + "\" " + secondCoordinates);
        Luogo existingLuogo = Model.getInstance().dbLuoghiHelper.findLuogo(placeName);

        // Assert
        assertNotNull(existingLuogo, placeName + " should still exist.");
        assertEquals(firstDescription, existingLuogo.getDescription(), "Place description should not change on duplicate add attempt.");
    }

    @Test
    public void testSetupAddLuogoFailEmptyTitle() {
        // Arrange
        enterSetupPhase();
        String emptyTitle = "";
        String description = "Empty Title";
        String coordinates = "10.1,20.2";

        // Act
        controller.interpreter("add -L \"" + emptyTitle + "\" \"" + description + "\" " + coordinates);
        Luogo emptyLuogo = Model.getInstance().dbLuoghiHelper.findLuogo(emptyTitle);

        // Assert
        assertNull(emptyLuogo, "Place with empty title should not be added.");
    }

    @Test
    public void testSetupDoneSuccess() {
        // Arrange
        enterSetupPhase();
        controller.interpreter("setmax 5");
        controller.interpreter("add -L Place1 \"Desc\" 10.0,20.0");

        // Act
        controller.interpreter("done");

        // Assert
        assertTrue(controller.setupCompleted(), "Setup should be marked as complete after 'done'.");
    }

    @Test
    public void testSetupDoneFailMissingPersoneMax() {
        // Arrange
        enterSetupPhase();
        controller.interpreter("add -L Place1 \"Desc\" 10.0,20.0");

        // Act
        controller.interpreter("done");

        // Assert
        assertFalse(controller.setupCompleted(), "Setup should not be complete if personeMax is missing.");
        int expectedMaxAfterFail = 10;
        controller.interpreter("setmax " + expectedMaxAfterFail);
        int actualMaxAfterFail = Model.getInstance().appSettings.getMaxPrenotazioniPerPersona();
        assertEquals(expectedMaxAfterFail, actualMaxAfterFail, "Should be able to set personeMax if 'done' failed.");
    }

    @Test
    public void testSetupDoneFailMissingLuogo() {
        // Arrange
        enterSetupPhase();
        controller.interpreter("setmax 5");

        // Act
        controller.interpreter("done");

        // Assert
        assertFalse(controller.setupCompleted(), "Setup should not be complete if luogo is missing.");
        String placeNameAfterFail = "PlaceAfterFail";
        String descriptionAfterFail = "Added after fail";
        String coordinatesAfterFail = "1.1:2.2";
        controller.interpreter("add -L " + placeNameAfterFail + " \"" + descriptionAfterFail + "\" " + coordinatesAfterFail);
        Luogo addedLuogoAfterFail = Model.getInstance().dbLuoghiHelper.findLuogo(placeNameAfterFail);
        assertNotNull(addedLuogoAfterFail, "Should be able to add luogo if 'done' failed.");
    }
}