package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;

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
        return controller.isActionDay16 || !controller.doneAll();
    }
}
