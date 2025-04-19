package V2.Ingsoft.controller.commands;

import V2.Ingsoft.controller.Controller;
import V2.Ingsoft.util.AssertionControl;
import V2.Ingsoft.view.ViewSE;

public abstract class AbstractCommand implements Command {

    protected final String CLASSNAME = this.getClass().getSimpleName();
    protected Controller controller;
    protected ListInterface commandInfo;
    protected boolean hasBeenExecuted = true; // by default, mark every command as already executed, so the 'and' check
    // on the execution of all setup commands succeeds
    // when creating ADD, SETMAX, SETAMBITO, LOGINSETUP,
    // CHANGEPSW, and DONE commands, set it to false, but only for these

    @Override
    public boolean canBeExecutedBy(int userPriority) {
        return commandInfo.canBeExecutedBy(userPriority);
    }

    @Override
    public boolean hasBeenExecuted() {
        return hasBeenExecuted; // SO THOSE IMPLEMENTED IN SETUP CAN MODIFY IT
    }

    protected boolean isExecutable() {
        if (controller.isActionDay16 || !controller.doneAll()) {
            return true;
        } else {
            String ERROR_16 = "Action possible only on the 16th of the month and not on holidays!";
            AssertionControl.logMessage(ERROR_16, 3, CLASSNAME);
            ViewSE.println(ERROR_16);
            return false;
        }
    }
}
