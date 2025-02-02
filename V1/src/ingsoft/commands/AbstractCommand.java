package ingsoft.commands;

import ingsoft.App;

public abstract class AbstractCommand implements Command {
    protected final CommandList commandInfo;
    protected final App app;

    protected AbstractCommand(App app, CommandList commandInfo) {
        this.app = app;
        this.commandInfo = commandInfo;
    }

    @Override
    public int getRequiredPermission() {
        return commandInfo.getRequiredPermission();
    }
}