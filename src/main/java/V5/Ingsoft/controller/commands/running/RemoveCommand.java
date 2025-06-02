package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.commands.running.list.CommandList;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;

public class RemoveCommand extends AbstractCommand {

    public RemoveCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.REMOVE;
        this.hasBeenExecuted = false;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (options == null || args == null) {
            return Payload.error(
                    "Internal error: invalid command structure.",
                    "Options or args are null in RemoveCommand"
            );
        }
        if (options.length < 1 || options[0].isBlank()) {
            return Payload.warn(
                    "Missing or invalid option for 'remove' command.",
                    "Missing or invalid option in RemoveCommand"
            );
        }

        char opt = options[0].charAt(0);
        String[] parsed = StringUtils.joinQuotedArguments(args);
        if (parsed == null || parsed.length < 1 || parsed[0].isBlank()) {
            return Payload.warn(
                    "Usage: remove -<c|f|v|T|L> <identifier>",
                    "Insufficient args for remove -" + opt
            );
        }
        String id = parsed[0];

        switch (opt) {
            case 'c': {
                // remove configurator
                if (Model.getInstance().dbConfiguratoreHelper.removeItem(id)) {
                    this.hasBeenExecuted = true;
                    return Payload.info(
                            "Configurator '" + id + "' removed successfully.",
                            "Removed configurator: " + id
                    );
                }
                return Payload.warn(
                        "Failed to remove configurator '" + id + "'. It might not exist.",
                        "Failed to remove configurator: " + id
                );
            }
            case 'f': {
                // remove fruitore
                if (Model.getInstance().removeFruitore(id)) {
                    this.hasBeenExecuted = true;
                    return Payload.info(
                            "Fruitore '" + id + "' removed successfully.",
                            "Removed fruitore: " + id
                    );
                }
                return Payload.warn(
                        "Failed to remove fruitore '" + id + "'. It might not exist.",
                        "Failed to remove fruitore: " + id
                );
            }
            case 'v': {
                // remove volontario
                if (!isExecutable()) {
                    return Payload.warn(
                            "Cannot remove volunteer: action not allowed now. Try Again on the 16th",
                            "RemoveCommand.removeVolontario: not executable"
                    );
                }

                Volontario vol = Model.getInstance().dbVolontarioHelper.getPersona(id);
                if (vol == null) {
                    return Payload.warn(
                            "Failed to remove volunteer '" + id + "'. It might not exist.",
                            "Failed to remove volunteer: " + id
                    );
                }
                Date when = controller.date.clone().addMonth(2);
                vol.setDeletionDate(when);
                this.hasBeenExecuted = true;
                return Payload.info(
                        "Volunteer '" + id + "' scheduled for removal on " + when + ".",
                        "Scheduled removal of volunteer: " + id
                );
            }
            case 'T': {
                // schedule removal of visit type
                if (!isExecutable()) {
                    return Payload.warn(
                            "Cannot schedule removal of visit type: action not allowed now. Try Again on the 16th",
                            "RemoveCommand.removeTipoVisita: not executable"
                    );
                }
                TipoVisita tv = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(id);
                if (tv == null) {
                    return Payload.warn(
                            "Visit type '" + id + "' not found.",
                            "RemoveCommand.removeTipoVisita: not found"
                    );
                }
                Date when = controller.date.clone().addMonth(2);
                tv.setDeletionDate(when);
                this.hasBeenExecuted = true;
                return Payload.info(
                        "Visit type '" + id + "' scheduled for removal on " + when + ".",
                        "Scheduled removal of TipoVisita: " + id
                );
            }
            case 'L': {
                // schedule removal of place
                if (!isExecutable()) {
                    return Payload.warn(
                            "Cannot schedule removal of place: action not allowed now. Try Again on the 16th",
                            "RemoveCommand.removeLuogo: not executable"
                    );
                }
                Luogo place = Model.getInstance().dbLuoghiHelper.findLuogo(id);
                if (place == null) {
                    return Payload.warn(
                            "Failed to remove place '" + id + "'. It might not exist.",
                            "Failed to remove place: " + id
                    );
                }
                Date when = controller.date.clone().addMonth(2);
                place.setDeletionDate(when);
                this.hasBeenExecuted = true;
                return Payload.info(
                        "Place '" + id + "' scheduled for removal on " + when + ".",
                        "Scheduled removal of Luogo: " + id
                );
            }
            default:
                return Payload.warn(
                        "Unknown option '-" + opt + "' for 'remove' command.",
                        "Unrecognized option in RemoveCommand: " + opt
                );
        }
    }
}
