package ingsoft.commands;

public abstract class AbstractCommand implements Command {
    protected ListInterface commandInfo;
    protected boolean hasBeenExecuted = false;

    @Override
    public boolean canPermission(int userPerm){
        return commandInfo.canPermission(userPerm);
    }

    @Override
    public boolean hasBeenExecuted(){
        return hasBeenExecuted;   //COSI QUELLI CHE VENGONO IMPLEMENTATI IN SETUP POSSONO MODIFICARLO
    }
}