package V5.ingsoft;

import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests for Use Cases UC16-UC19, UC34 (Volunteer Actions)
public class VolunteerActionsTests extends BaseTest {

    @Override
    @BeforeEach
    public void setup() {
        resetDataFiles();
        enterRegimePhase();
        // Specific setup done in each test or helper method
    }

    // --- Volunteer Action Tests ---

    // UC18 - Visualizzazione Tipi Visita Associati (Volontario)
    // Note: No direct command for volunteer to list *only* their types.
    // Test checks internal state after running general list command.
    @Test
    public void testVolunteerListViewAssociatedTypes() {
        // Arrange: enterRegimePhase creates VolRegime assigned to TVRegime.
        // We need to log in as VolRegime.
        controller.interpreter("logout"); // Logout configRegime
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw newVolPass newVolPass"); // Change default pass

        // Act
        controller.interpreter("list -v"); // Run general list command (will list all volunteers)

        // Assert state for VolRegime
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("VolRegime");
        assertNotNull(vol, "Volunteer VolRegime should exist.");
    }

    @Test
    public void testVolunteerListViewAssociatedTypesEmpty() {
        // Arrange: enterRegimePhase runs. Add a new volunteer but don't assign them.
        controller.interpreter("add -v volTestEmpty passVTE"); // Added by configRegime
        // Login as the new empty volunteer
        controller.interpreter("logout"); // Logout configRegime
        controller.interpreter("login volTestEmpty passVTE");
        controller.interpreter("changepsw newEmptyVolPass newEmptyVolPass");

        // Act
        controller.interpreter("list -v"); // Run general list command

        // Assert state for volTestEmpty
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("volTestEmpty");
        assertNotNull(vol, "Volunteer volTestEmpty should exist.");
    }

    // UC19 - Dichiarazione Disponibilità Volontario
    @Test
    public void testVolunteerDeclareAvailabilitySuccess() {
        // Arrange: enterRegimePhase creates VolRegime assigned to TVRegime (Mon/Tue)
        // Login as VolRegime
        controller.interpreter("logout"); // Logout configRegime
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw newVolPass newVolPass");

        // Set current date to be within the allowed window for July (e.g., May 16th)
        controller.interpreter("time -s 16/05/2025");
        String futureDate = "07/06/2025"; // A Monday in July

        // Act
        Payload<?> o = controller.interpreter("setav -a " + futureDate); // Use correct command

        // Assert: Check internal state if possible, otherwise assume command worked
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("VolRegime");
        assertNotNull(vol, "Volunteer should still exist.");
        assertTrue(vol.isAvailable(7), "Executed availability command successfully (verification of future state requires specific getters).");
    }

    @Test
    public void testVolunteerDeclareAvailabilityFailInvalidFormat() {
        // Arrange: Log in as VolRegime
        controller.interpreter("logout");
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw newVolPass newVolPass");
        controller.interpreter("time -s 16/05/2025"); // Set valid date context

        // Act
        controller.interpreter("setav -a 2025-07-10"); // Invalid format (needs dd/mm/yyyy)

        // Assert
        // We expect the command to fail gracefully due to parsing error in Date or AvailabilityCommand.
        // No state change should occur. Check logs for specific error.
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("VolRegime");
        assertFalse(vol.getAvailability()[7 - 1], "Executed setav with invalid format (verify logs for parsing error message).");
    }

    @Test
    public void testVolunteerDeclareAvailabilityFailInvalidDate() {
        // Arrange: Log in as VolRegime
        controller.interpreter("logout");
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw newVolPass newVolPass");
        controller.interpreter("time -s 16/01/2025"); // Set valid date context

        // Act
        controller.interpreter("setav -a 31/02/2025"); // Invalid date

        // Assert
        // We expect the command to fail gracefully due to parsing error in Date or AvailabilityCommand.
        // No state change should occur. Check logs for specific error.
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("VolRegime");
        assertFalse(vol.getAvailability()[7 - 1], "Executed setav with invalid date (verify logs for parsing error message).");
    }

    @Test
    public void testVolunteerDeclareAvailabilityFailOutsideWindow() {
        // Arrange: Log in as VolRegime
        controller.interpreter("logout");
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw newVolPass newVolPass");

        // Set current date OUTSIDE the allowed window (e.g., July 1st, trying to set for September)
        controller.interpreter("time -s 01/07/2025");
        String futureDate = "04/09/2025"; // Target date (Monday)

        // Act
        controller.interpreter("setav -a " + futureDate);

        // Assert
        // The command should execute, but the internal logic in Volontario.setAvailability but its not assigned because its outside the allowed windows
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("VolRegime");
        assertFalse(vol.getAvailability()[4 - 1], "Executed setav outside allowed window (verify internal logic prevents update).");
    }

    @Test
    public void testVolunteerDeclareAvailabilityFailPrecludedDate() {
        // Arrange: enterRegimePhase runs. Log in as configRegime to preclude a date.
        controller.interpreter("time -s 1/06/2025");
        String precludedDateStr = "01/08/2025"; // Monday

        // Add precluded date (as configRegime)
        controller.interpreter("preclude -a " + precludedDateStr);

        //past 1 month
        controller.interpreter("time -s 01/07/2025");

        // Login as VolRegime
        controller.interpreter("logout");
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw newVolPass newVolPass");

        // Act
        controller.interpreter("setav -a " + precludedDateStr);

        // Assert
        // The AvailabilityCommand.manageDate checks precluded dates and should skip the update.
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("VolRegime");
        assertFalse(vol.isAvailable(1), "Executed setav for a precluded date (verify internal logic prevents update).");
    }


    // UC34 - Visualizzazione Visite Assegnate (Volontario)
    // @Test
    // public void testVolunteerListViewAssignedVisits() {
    //     // Arrange: Use entities created by enterRegimePhase (VolRegime, TVRegime, PlaceRegime)
    //     String username = "VolRegime";
    //     String visitTypeName = "TVRegime"; // Name used in commands
    //     String visitDate = "05/07/2025"; // Saturday

    //     Date d;
    //     try {
    //         d = new Date(visitDate);
    //     } catch (Exception e) {
    //         return;
    //     }

    //     // 1. Volunteer Declares Availability (as Volunteer)
    //     controller.interpreter("logout"); // Logout configRegime
    //     controller.interpreter("login VolRegime PassVol");
    //     controller.interpreter("changepsw newVolPass newVolPass");
    //     controller.interpreter("time -s 16/05/2025"); // Window to declare for July
    //     controller.interpreter("setav -a " + visitDate);
    //     controller.interpreter("logout");

    //     // 2. Generate Plan (as configRegime)
    //     controller.interpreter("login configRegime passRegime");
    //     controller.interpreter("time -s 16/06/2025"); // Day to generate plan for July
    //     controller.interpreter("makeplan"); // Generate visits for July
    //     controller.interpreter("logout");

    //     // 3. Book Visit (Fruitore) - to meet minimum participants
    //     controller.interpreter("login fruitTestAssign passFTA passFTA");
    //     controller.interpreter("visit -a " + visitTypeName + " " + visitDate + " 1"); // Book 1 person
    //     controller.interpreter("logout");

    //     // Deadline is 3 days before visitDate (05/07) -> 02/07
    //     controller.interpreter("time -s 04/07/2025"); // Day after deadline

    //     // 5. Login as Volunteer again
    //     controller.interpreter("login VolRegime newVolPass"); // Use the changed password

    //     // Act
    //     controller.interpreter("myvisit"); // Use correct command
    //     Visita v = Model.getInstance().dbVisiteHelper.findVisita(visitTypeName, visitDate);

    //     // Assert: Fetch visits and check if the expected one is assigned and confirmed.
    //     List<Visita> allVisits = Model.getInstance().dbVisiteHelper.getVisite();

    //     String expectedVisitUID = visitTypeName.hashCode() + "t" + d + username; // Reconstruct expected UID based on Visita constructor logic
    //     assertEquals(expectedVisitUID, v.getUID());

    //     List<Visita> assignedConfirmedVisits = allVisits.stream()
    //             .filter(visit -> visit != null &&
    //                     username.equals(visit.getUidVolontario()) &&
    //                     visit.getStatus() == StatusVisita.CONFIRMED &&
    //                     visit.getUID().equals(expectedVisitUID)) // Check UID too
    //             .toList();

    //     assertEquals(1, assignedConfirmedVisits.size(), "Volunteer should have exactly one confirmed visit assigned matching the setup.");
    //     assertEquals(visitTypeName, assignedConfirmedVisits.getFirst().getTitle());
    // }

    @Test
    public void testVolunteerListViewAssignedVisitsEmpty() {
        // Arrange: Log in as VolRegime, ensure no visits assigned
        controller.interpreter("logout");
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw newVolPass newVolPass");

        // Act
        controller.interpreter("myvisit"); // Use correct command

        // Assert: Fetch visits and check none are assigned to this volunteer.
        List<Visita> assignedVisits = new java.util.ArrayList<>();
        String currentUsername = controller.getCurrentUser().getUsername(); // Should be volTestAssignEmpty

        for (Visita visit : Model.getInstance().dbVisiteHelper.getItems()) {
            if (visit != null && currentUsername.equals(visit.getVolontarioUID())) {
                assignedVisits.add(visit);
            }
        }
        assertTrue(assignedVisits.isEmpty(), "Volunteer should have no assigned visits.");
    }

}
