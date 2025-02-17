package ingsoft.commands;

import ingsoft.App;
import ingsoft.commands.running.CommandList;

public abstract class AbstractCommand implements Command {
    protected final CommandList commandInfo;
    protected final App app;
    protected boolean hasBeenExecuted = false;

    protected AbstractCommand(App app, CommandList commandInfo) {
        this.app = app;
        this.commandInfo = commandInfo;
    }

    @Override
    public boolean canPermission(int userPerm){
        return commandInfo.canPermission(userPerm);
    }

    public boolean hasBeenExecuted(){
        return hasBeenExecuted;   //COSI QUELLI CHE VENGONO IMPLEMENTATI IN SETUP POSSONO MODIFICARLO
    }
}