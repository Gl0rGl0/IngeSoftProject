package V4.Ingsoft.controller.commands;

public interface Command {
    /**
     * Executes the command given the array of options and the array of arguments.
     *
     * @param options the options (without the initial hyphen)
     * @param args    the arguments
     */
    void execute(String[] options, String[] args);

    boolean canBeExecutedBy(int userPriority);

    boolean hasBeenExecuted();
}
