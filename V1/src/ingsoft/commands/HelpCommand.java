package ingsoft.commands;

import ingsoft.App;
import ingsoft.util.ViewSE;

public class HelpCommand extends AbstractCommand{
    public HelpCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    public void execute(String[] options, String[] args) {
        ViewSE.log(super.commandInfo.getHelpMessage(app.user.type().getPriorita()));
    }
}
