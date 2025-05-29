package V5.Ingsoft.util.interpreter;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.util.Payload;

public class InterpreterContext {
    private Interpreter current;
    private Controller controller;

    public InterpreterContext(Controller c){
        this.controller = c;

        current = new SetupInterpreter(c);
    }

    public void switchInterpreter(){
        if(current instanceof RunningInterpreter) return;

        this.current = new RunningInterpreter(controller);
    }

    public boolean checkSetup() {
        return current.isSetupCompleted();
    }

    public boolean hasExecutedAllCommands() {
        return current.hasExecutedAllCommands();
    }

    public Payload<?> interpret(String prompt, Persona currentUser) {
        return current.interpret(prompt, currentUser);
    }
}
