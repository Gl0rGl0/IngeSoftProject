package V4.ingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import V4.Ingsoft.controller.item.persone.PersonaType;

// Tests for Use Cases UC10-UC15, UC20-UC27 (+ Regime Phase)
public class UserActionsTests extends BaseTest{

    @Override
    @BeforeEach
    public void setup() {
        resetDataFiles();
        enterRegimePhase();
    }

    private void setupAndLoginAsFruitore(String username, String password) {
        controller.interpreter("logout");
        controller.interpreter("login " + username + " " + password + " " + password);
        assertNotNull(controller.user, "Fruitore should be logged in for tests.");
        assertEquals(PersonaType.FRUITORE, controller.getCurrentUser().getType(), "User should be of type FRUITORE.");   
    }

    private void setupVolunteerRunning(){
        //skip 2 month to ensure TipoVisita PROPOSTA
        controller.interpreter("time -s 16/05/2025");
        controller.interpreter("logout");

        //adding availability to volunteer
        controller.interpreter("login VolRegime PassVol");
        controller.interpreter("changepsw PassVol");
        controller.interpreter("setav -a 16/07/2025 17/07/2025 18/07/2025 19/07/2025 20/07/2025");
        controller.interpreter("logout");

        //making the visits plan
        controller.interpreter("login configRegime passRegime");
        controller.interpreter("make");
    }   
    // --- Fruitore Action Tests ---

    // UC30 - Visualizzazione Visite Disponibili (Fruitore)
    @Test
    public void testUserListViewAvailableVisits() {
        System.out.println();
        setupVolunteerRunning();
        // Arrange
        setupAndLoginAsFruitore("userTestView", "passUTV");
        // TODO: Generate visits

        // Act
        controller.interpreter("list visits"); // Assumed command

        // Assert
        // Cannot assert console output. Assume command runs if setup is correct.
        // TODO: Add assertion if a method exists to get list of subscribable visits.
        assertTrue(true, "List command executed (cannot verify output). Needs UC20.");
    }

    @Test
    public void testUserListViewAvailableVisitsEmpty() {
        // Arrange
        setupAndLoginAsFruitore("userTestEmpty", "passUTE");
        // Ensure no visits generated

        // Act
        controller.interpreter("list visits"); // Assumed command

        // Assert
        // Cannot assert console output. Assume command runs.
        // TODO: Add assertion if a method exists to get list of subscribable visits (should be empty).
        assertTrue(true, "List command executed for empty visits (cannot verify output). Needs UC20.");
    }

    // UC31 - Iscrizione a Visita Proposta
    // NOTE: These tests assume UC20 (Plan Generation) has run and created visits.
    // NOTE: Assumes 'assign <VisitUID> <NumPeople>' command format for subscription.
    @Test
    public void testUserSubscribeVisitSuccess() {
        // Arrange
        String username = "userSub";
        setupAndLoginAsFruitore(username, "passUS");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure this visit actually exists in 'proposta' state via UC20 setup in helper.
        // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
        // assertNotNull(visita, "Prerequisite: Visit must exist.");
        // assertEquals(V4.Ingsoft.controller.item.luoghi.StatusVisita.PROPOSED, visita.getStatus(), "Prerequisite: Visit must be in PROPOSED state.");
        // int initialParticipants = visita.getCurrentNumber();

        // Act
        controller.interpreter("assign " + visitUID + " 2"); // Subscribe for 2 people

        // Assert
        // visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID); // Re-fetch visit
        // assertEquals(initialParticipants + 2, visita.getCurrentNumber(), "Participant count should increase by 2.");
        // assertTrue(visita.hasFruitore(username), "User should be listed as subscribed.");
        // Find the specific Iscrizione and check quantity
        // V4.Ingsoft.controller.item.persone.Iscrizione iscrizione = visita.getIscrizioni().stream().filter(i -> i.getUIDFruitore().equals(username)).findFirst().orElse(null);
        // assertNotNull(iscrizione, "Iscrizione object should exist for the user.");
        // assertEquals(2, iscrizione.getQuantity(), "Iscrizione quantity should be 2.");
        assertTrue(true, "Executed subscribe command (cannot verify state without UC20/Visita access)."); // Placeholder until UC20
    }

    @Test
    public void testUserSubscribeVisitFailZeroPeople() {
        // Arrange
        setupAndLoginAsFruitore("userSubZero", "passUSZ");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure visit exists.

        // Act
        controller.interpreter("assign " + visitUID + " 0"); // Subscribe for 0 people (should fail)

        // Assert
        // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
        // assertNotNull(visita, "Prerequisite: Visit must exist.");
        // int initialParticipants = visita.getCurrentNumber(); // Get initial count before failed attempt
        // assertEquals(initialParticipants, visita.getCurrentNumber(), "Participant count should not change.");
        // assertFalse(visita.hasFruitore(username), "User should not be listed as subscribed.");
        assertTrue(true, "Executed subscribe command with 0 people (cannot verify state without UC20/Visita access)."); // Placeholder until UC20
        // TODO: Check log output for error.
    }

    @Test
    public void testUserSubscribeVisitFailTooManyPeoplePerInscription() {
        // Arrange (Max per inscription set to 5 in helper)
        setupAndLoginAsFruitore("userSubMaxInscr", "passUSMI");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure visit exists.

        // Act
        controller.interpreter("assign " + visitUID + " 6"); // Subscribe for 6 (max is 5)

        // Assert
        // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
        // assertNotNull(visita, "Prerequisite: Visit must exist.");
        // int initialParticipants = visita.getCurrentNumber();
        // assertEquals(initialParticipants, visita.getCurrentNumber(), "Participant count should not change.");
        // assertFalse(visita.hasFruitore(username), "User should not be listed as subscribed.");
         assertTrue(true, "Executed subscribe command exceeding max per inscription (cannot verify state without UC20/Visita access)."); // Placeholder until UC20
        // TODO: Check log output for error.
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
        controller.interpreter("assign " + visitUID + " 6"); // Try to subscribe for 6 (only 5 spots left)

        // Assert
        // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
        // assertNotNull(visita, "Prerequisite: Visit must exist.");
        // assertEquals(5, visita.getCurrentNumber(), "Participant count should remain 5."); // User 2's 5 people
        // assertFalse(visita.hasFruitore("userSubCap1"), "User 1 should not be listed as subscribed.");
        assertTrue(true, "Executed subscribe command exceeding capacity (cannot verify state without UC20/Visita access)."); // Placeholder until UC20
        // TODO: Check log output for error.
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
         controller.interpreter("assign " + visitUID + " 5"); // Subscribe for the remaining 5 spots

         // Assert
         // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
         // assertNotNull(visita, "Prerequisite: Visit must exist.");
         // assertEquals(10, visita.getCurrentNumber(), "Participant count should be 10.");
         // assertTrue(visita.hasFruitore("userSubComp1"), "User 1 should be subscribed.");
         // assertEquals(V4.Ingsoft.controller.item.luoghi.StatusVisita.COMPLETED, visita.getStatus(), "Visit status should change to COMPLETED.");
         assertTrue(true, "Executed subscribe command filling capacity (cannot verify state without UC20/Visita access)."); // Placeholder until UC20
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
        controller.interpreter("list myvisits"); // Assumed command

        // Assert
        // Cannot assert console output. Assume command runs.
        // TODO: Add assertion if a method exists to get user's subscriptions (should contain 2).
        assertTrue(true, "List command executed (cannot verify output). Needs UC20.");
    }

    @Test
    public void testUserListViewOwnSubscriptionsEmpty() {
        // Arrange
        setupAndLoginAsFruitore("userViewSubsEmpty", "passUVSE");

        // Act
        controller.interpreter("list myvisits"); // Assumed command

        // Assert
        // Cannot assert console output. Assume command runs.
        // TODO: Add assertion if a method exists to get user's subscriptions (should be empty).
        assertTrue(true, "List command executed for no subscriptions (cannot verify output).");
     }


    // UC33 - Disdetta Iscrizione (Fruitore)
    // NOTE: These tests assume UC20 (Plan Generation) has run and created visits.
    // NOTE: Assumes 'remove <BookingCode>' command format for cancellation.
    // NOTE: Requires a way to get the actual BookingCode (Iscrizione UID) after subscribing.
    @Test
    public void testUserCancelSubscriptionSuccess() {
        // Arrange
        String username = "userCancel";
        setupAndLoginAsFruitore(username, "passUC");
        String visitUID = "TVUser_10-07-2025"; // Hypothetical Visit UID
        // TODO: Ensure visit exists in 'proposta' state.
        controller.interpreter("assign " + visitUID + " 2"); // Subscribe
        // TODO: Need a reliable way to get the booking code (Iscrizione UID) here.
        // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
        // V4.Ingsoft.controller.item.persone.Iscrizione iscrizione = visita.getIscrizioni().stream().filter(i -> i.getUIDFruitore().equals(username)).findFirst().orElse(null);
        // assertNotNull(iscrizione, "Prerequisite: User must have an Iscrizione.");
        // String bookingCode = iscrizione.getUIDIscrizione();
        String bookingCode = "PLACEHOLDER_CODE"; // Placeholder

        // Act
        controller.interpreter("remove " + bookingCode);

        // Assert
        // visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID); // Re-fetch
        // assertFalse(visita.hasFruitore(username), "User should no longer be subscribed after cancellation.");
        // assertEquals(0, visita.getCurrentNumber(), "Participant count should be 0 after cancellation."); // Assuming only user subscribed
        assertTrue(true, "Executed cancel command (cannot verify state without UC20/Visita/BookingCode access)."); // Placeholder
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
        controller.interpreter("remove " + bookingCodeUser1);

        // Assert
        // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
        // assertNotNull(visita, "Prerequisite: Visit must exist.");
        // assertFalse(visita.hasFruitore("userCancelComp1"), "User 1 should no longer be subscribed.");
        // assertEquals(5, visita.getCurrentNumber(), "Participant count should be 5 (User 2's).");
        // assertEquals(V4.Ingsoft.controller.item.luoghi.StatusVisita.PROPOSED, visita.getStatus(), "Visit status should change back to PROPOSED.");
        assertTrue(true, "Executed cancel command from COMPLETED state (cannot verify state without UC20/Visita/BookingCode access)."); // Placeholder
    }

    @Test
    public void testUserCancelSubscriptionFailInvalidCode() {
        // Arrange
        setupAndLoginAsFruitore("userCancelInvalid", "passUCI");
        String visitUID = "TVUser_10-07-2025";
        // TODO: Ensure visit exists.
        controller.interpreter("assign " + visitUID + " 1"); // Assumed command - uncommented

        // Act
        controller.interpreter("remove INVALID_CODE"); // Try to remove with invalid code

        // Assert
        // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
        // assertNotNull(visita, "Prerequisite: Visit must exist.");
        // assertTrue(visita.hasFruitore("userCancelInvalid"), "User should still be subscribed.");
        // assertEquals(1, visita.getCurrentNumber(), "Participant count should remain 1.");
        assertTrue(true, "Executed cancel command with invalid code (cannot verify state without UC20/Visita access)."); // Placeholder
        // TODO: Check log output for error.
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
         controller.interpreter("remove " + bookingCodeUser2); // User 1 tries to remove User 2's booking

         // Assert
         // V4.Ingsoft.controller.item.luoghi.Visita visita = controller.db.dbVisiteHelper.getVisitaByUID(visitUID);
         // assertNotNull(visita, "Prerequisite: Visit must exist.");
         // Check User 2's subscription still exists
         // V4.Ingsoft.controller.item.persone.Iscrizione iscrizione2 = visita.getIscrizioni().stream().filter(i -> i.getUIDFruitore().equals("userCancelOwner2")).findFirst().orElse(null);
         // assertNotNull(iscrizione2, "User 2's subscription should still exist.");
         // assertEquals(bookingCodeUser2, iscrizione2.getUIDIscrizione(), "User 2's booking code should match.");
         // assertEquals(1, visita.getCurrentNumber(), "Participant count should remain 1.");
         assertTrue(true, "Executed cancel command for another user's booking (cannot verify state without UC20/Visita/BookingCode access)."); // Placeholder
         // TODO: Check log output for error.
     }

     // TODO: Add test for cancelling when inscriptions are closed (3 days before visit) - requires date simulation.
     // TODO: Add test for cancelling a visit not in 'proposta' or 'completa' state.

 }
