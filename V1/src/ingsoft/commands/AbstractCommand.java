package ingsoft.commands;

import ingsoft.App;

public abstract class AbstractCommand implements Command {
    protected final ListInterface commandInfo;
    protected final App app;
    protected boolean hasBeenExecuted = false;

    protected AbstractCommand(App app, ListInterface commandInfo) {
        this.app = app;
        this.commandInfo = commandInfo;
    }

    @Override
    public boolean canPermission(int userPerm){
        return commandInfo.canPermission(userPerm);
    }

    @Override
    public boolean hasBeenExecuted(){
        return hasBeenExecuted;   //COSI QUELLI CHE VENGONO IMPLEMENTATI IN SETUP POSSONO MODIFICARLO
    }
}