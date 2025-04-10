package V4.Ingsoft.controller.commands.running;

import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.StatusVisita;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Configuratore;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class AddCommand extends AbstractCommand {
    private final Controller controller;

    public AddCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.ADD;
    }

    @Override
    /**
     * Implementation of the "add" command.
     *
     * @param options the options (e.g., -c for configurator)
     * @param args    any additional arguments
     */
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Error using the 'add' command");
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Error using the 'add' command", 2,
                    CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'c' -> addConfiguratore(args);
            case 'v' -> addVolontario(args);
            case 'L' -> addLuogo(args);
            case 't' -> addTipoVisita(args);
            default -> ViewSE.println("Option not recognized for 'add'.");
        }
    }

    private void addConfiguratore(String[] args) {
        Configuratore c;
        try {
            c = new Configuratore(args);
        } catch (Exception e) {
            AssertionControl.logMessage("Error while creating fruitore", 2, CLASSNAME);
            return;
        }
        // adds a new configurator who will need to change password on first login
        if (args.length > 1 && controller.db.dbConfiguratoreHelper.addConfiguratore(c)) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added configurator: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Configurator not added", 2, CLASSNAME);
        }
    }

    private void addVolontario(String[] args) {
        Volontario v;
        try {
            v = new Volontario(args);
        } catch (Exception e) {
            AssertionControl.logMessage("Error while creating fruitore", 2, CLASSNAME);
            return;
        }
        // adds a new volunteer who will need to change password on first login
        if (args.length > 1 && controller.db.dbVolontarioHelper.addVolontario(v)) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added volunteer: " + args[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Volunteer not added", 3, CLASSNAME);
        }
    }

    private void addTipoVisita(String[] args) {

        String[] a = StringUtils.joinQuotedArguments(args);

        if (!controller.isActionDay16) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Cannot add a visit type if it's not the 16th of the month: " + a[0],
                    1,
                    CLASSNAME);
            return;
        }
        
        TipoVisita t;
        try {
            t = new TipoVisita(a, controller.date);
        } catch (Exception e) {
            AssertionControl.logMessage(e.getMessage(), 2, CLASSNAME);
            return;
        }

        if (controller.db.dbTipoVisiteHelper.addTipoVisita(t)) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added TipoVisita: " + a[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| TipoVisita not added: " + a[0], 3, CLASSNAME);
        }
    }

    private void addLuogo(String[] args) {

        String[] a = StringUtils.joinQuotedArguments(args);

        if (!controller.isActionDay16) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Cannot add a place if it's not the 16th of the month: "
                            + a[0],
                    1,
                    CLASSNAME);
            return;
        }

        Luogo l;
        try {
            l = new Luogo(a);
        } catch (Exception e) {
            AssertionControl.logMessage(e.getMessage(), 2, CLASSNAME);
            return;
        }

        if (a.length > 2 && controller.db.dbLuoghiHelper.addLuogo(l)) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added place: " + a[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Place not added: " + a[0], 3, CLASSNAME);
        }
    }

    

}
