package V3.Ingsoft.controller.commands.setup;

import V3.Ingsoft.controller.Controller;
import V3.Ingsoft.controller.commands.AbstractCommand;
import V3.Ingsoft.util.AssertionControl;
import V3.Ingsoft.view.ViewSE;

public class DoneCommandSETUP extends AbstractCommand {

    public DoneCommandSETUP(Controller c) {
        super.commandInfo = CommandListSETUP.DONE;
        this.controller = c;
        this.hasBeenExecuted = false;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (controller.haveAllBeenExecuted()) {
            ViewSE.println("Done executed correctly, switching to running mode");
            this.hasBeenExecuted = true;
            controller.switchInterpreter();
        } else {
            AssertionControl.logMessage("Non puoi eseguire il done se non hai eseguito tutti i comandi precedenti", 3, CLASSNAME);
        }
    }
}