package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;

public class AddCommand extends AbstractCommand {

    public AddCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.ADD;
        this.hasBeenExecuted = false;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (options == null || args == null) {
            return Payload.error(
                "Internal error: invalid command structure.",
                "Options or args are null in AddCommand" );
        }
        if (options.length < 1 || options[0].isBlank()) {
            return Payload.warn(
                "Missing or invalid option for 'add' command.",
                "Missing or invalid option in AddCommand" );
        }
        char opt = options[0].charAt(0);
        return switch (opt) {
            case 'c' -> handleAddConfiguratore(args);
            case 'v' -> handleAddVolontario(args);
            case 'L' -> handleAddLuogo(args);
            case 'T' -> handleAddTipoVisita(args);
            default  -> Payload.warn(
                    "Unknown option '-" + opt + "' for 'add' command.",
                    "Unrecognized option in AddCommand: " + opt );
        };
    }

    private Payload<String> handleAddConfiguratore(String[] args) {
        if (args.length < 2) {
            return Payload.warn(
                "Usage: add -c <username> <password>",
                "Insufficient args for add -c" );
        }
        String username = args[0];
        Configuratore c;
        try {
            c = new Configuratore(new String[]{username, args[1]});
        } catch (Exception e) {
            return Payload.error(
                "Error creating configurator: " + e.getMessage(),
                "Exception in addConfiguratore: " + e.getMessage() );
        }
        if (Model.getInstance().dbConfiguratoreHelper.addItem(c)) {
            this.hasBeenExecuted = true;
            return Payload.info(
                "Configurator '" + username + "' added successfully.",
                "Added configurator: " + username );
        }
        return Payload.warn(
            "Failed to add configurator '" + username + "'. It might already exist.",
            "Failed to add configurator: " + username );
    }

    private Payload<String> handleAddVolontario(String[] args) {
        if (args.length < 2) {
            return Payload.warn(
                "Usage: add -v <username> <password>",
                "Insufficient args for add -v" );
        }
        String volName = args[0];
        Volontario v;
        try {
            v = new Volontario(new String[]{volName, args[1]}, controller.date.clone());
        } catch (Exception e) {
            return Payload.error(
                "Error creating volunteer: " + e.getMessage(),
                "Exception in addVolontario: " + e.getMessage() );
        }
        if (Model.getInstance().dbVolontarioHelper.addItem(v)) {
            this.hasBeenExecuted = true;
            return Payload.info(
                "Volunteer '" + volName + "' added successfully.",
                "Added volunteer: " + volName );
        }
        return Payload.warn(
            "Failed to add volunteer '" + volName + "'. It might already exist.",
            "Failed to add volunteer: " + volName );
    }

    private Payload<String> handleAddLuogo(String[] args) {
        String[] parsed = StringUtils.joinQuotedArguments(args);
        if (parsed == null || parsed.length < 3) {
            return Payload.warn(
                "Usage: add -L \"<name>\" \"<description>\" <location>",
                "Insufficient args for add -L" );
        }
        String placeName = parsed[0];
        Luogo l;
        try {
            l = new Luogo(parsed, controller.date.clone());
        } catch (Exception e) {
            return Payload.error(
                "Error creating place: " + e.getMessage(),
                "Exception in addLuogo: " + e.getMessage() );
        }
        if (Model.getInstance().dbLuoghiHelper.addItem(l)) {
            this.hasBeenExecuted = true;
            return Payload.info(
                "Place '" + placeName + "' added successfully.",
                "Added place: " + placeName );
        }
        return Payload.warn(
            "Failed to add place '" + placeName + "'. It might already exist.",
            "Failed to add place: " + placeName );
    }

    private Payload<String> handleAddTipoVisita(String[] args) {
        String[] parsed = StringUtils.joinQuotedArguments(args);
        if (parsed == null || parsed.length < 11) {
            return Payload.warn(
                "Usage: add -T <type details> (11 args required)",
                "Insufficient args for add -T" );
        }
        String title = parsed[0];
        TipoVisita t;
        try {
            t = new TipoVisita(parsed, controller.date.clone());
        } catch (Exception e) {
            return Payload.error(
                "Error creating visit type: " + e.getMessage(),
                "Exception in addTipoVisita: " + e.getMessage() );
        }
        if (Model.getInstance().dbTipoVisiteHelper.addItem(t)) {
            this.hasBeenExecuted = true;
            return Payload.info(
                "Visit type '" + title + "' added successfully.",
                "Added TipoVisita: " + title );
        }
        return Payload.warn(
            "Failed to add visit type '" + title + "'. It might already exist.",
            "Failed to add TipoVisita: " + title );
    }
}