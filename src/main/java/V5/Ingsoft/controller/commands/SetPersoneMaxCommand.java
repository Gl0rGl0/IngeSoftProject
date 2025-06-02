package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.setup.list.CommandListSETUP;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.AppSettings;
import V5.Ingsoft.util.JsonStorage;
import V5.Ingsoft.util.Payload;

import java.util.Objects;

public class SetPersoneMaxCommand extends AbstractCommand {

    public SetPersoneMaxCommand(Controller controller, boolean hasBeenExecuted) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.SETMAX;
        this.hasBeenExecuted = hasBeenExecuted;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (args == null || args.length < 1)
            return Payload.error(
                    "Usage: setmax <number>",
                    "Missing argument for SetPersoneMaxCommand");
        int max;
        try {
            max = Integer.parseInt(args[0]);
            if (max < 1)
                return Payload.warn(
                        "Invalid value: must be at least 1",
                        "Attempted to set max < 1");
        } catch (NumberFormatException e) {
            return Payload.warn(
                    "Invalid value: please enter a number",
                    "NumberFormatException in SetPersoneMaxCommand: " + e.getMessage());
        }

        Objects.requireNonNull(Model.getInstance().appSettings).setMaxPrenotazioniPerPersona(max);
        boolean saved = JsonStorage.saveObject(AppSettings.PATH, Model.getInstance().appSettings);
        if (!saved)
            return Payload.error(
                    "Error saving settings",
                    "JsonStorage.saveObject failed in SetPersoneMaxCommand");

        this.hasBeenExecuted = true;
        return Payload.info(
                "Maximum bookings per person set to: " + max,
                "SetPersoneMaxCommand executed successfully");
    }
}