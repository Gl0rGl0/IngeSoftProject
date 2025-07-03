package V1.Ingsoft.controller.commands;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.setup.CommandListSETUP;
import V1.Ingsoft.model.Model;
import V1.Ingsoft.util.AppSettings;
import V1.Ingsoft.util.JsonStorage;
import V1.Ingsoft.view.ViewSE;

public class SetPersoneMaxCommand extends AbstractCommand {

    //Se uso il singleton per il Model non mi serve

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
            Model.appSettings.setMaxPrenotazioniPerPersona(max);
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
