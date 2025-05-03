package V5.Ingsoft.controller.commands;

import V5.Ingsoft.util.Payload;

public interface Command {
    /**
     * Executes the command given the array of options and the array of arguments.
     *
     * @param options the options (without the initial hyphen)
     * @param args    the arguments
     */
    Payload execute(String[] options, String[] args);

    boolean canBeExecutedBy(int userPriority);

    boolean hasBeenExecuted();
}
