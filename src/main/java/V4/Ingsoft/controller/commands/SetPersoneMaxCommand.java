package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.setup.CommandListSETUP;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.AppSettings; // Import AppSettings
import V4.Ingsoft.util.JsonStorage; // Import JsonStorage
import V4.Ingsoft.view.ViewSE;

public class SetPersoneMaxCommand extends AbstractCommand {

    private final Controller controller;    //Se uso il singleton per il Model non mi serve

    public SetPersoneMaxCommand(Controller controller, boolean hasBeenExecuted) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.SETMAX;
        this.hasBeenExecuted = hasBeenExecuted;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Error in prompt usage");
            return;
        }
        int max;
        try {
            max = Integer.parseInt(args[0]);
            if (max < 1) {
                ViewSE.println("Invalid value. Maximum must be at least 1.");
                return;
            }
            // Set the value in AppSettings
            controller.getDB().appSettings.setMaxPrenotazioniPerPersona(max);
            // Save the updated settings
            if (JsonStorage.saveObject(AppSettings.PATH, Model.appSettings)) {
                 ViewSE.println("Maximum bookings per person set to: " + max);
            } else {
                 ViewSE.println("Error saving settings.");
            }
        } catch (NumberFormatException e) {
            ViewSE.println("Invalid value. Please enter a number.");
            return;
        }
        this.hasBeenExecuted = true; // MUST ALSO BE EXECUTED DURING SETUP
    }
}
