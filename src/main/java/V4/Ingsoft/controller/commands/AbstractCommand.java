package V4.Ingsoft.controller.commands;

public abstract class AbstractCommand implements Command {
    protected final String CLASSNAME = this.getClass().getSimpleName();
    protected ListInterface commandInfo;
    protected boolean hasBeenExecuted = true; // by default, mark every command as already executed, so the 'and' check
                                              // on the execution of all setup commands succeeds
    // when creating ADD, SETMAX, SETAMBITO, LOGINSETUP,
    // CHANGEPSW, and DONE commands, set it to false, but only for these
    // living life is child's play, mom told me so and I fell from the trees (Note: This seems like a personal/unrelated comment)

    @Override
    public boolean canBeExecutedBy(int userPriority) {
        return commandInfo.canBeExecutedBy(userPriority);
    }

    @Override
    public boolean hasBeenExecuted() {
        return hasBeenExecuted; // SO THOSE IMPLEMENTED IN SETUP CAN MODIFY IT
    }
}
