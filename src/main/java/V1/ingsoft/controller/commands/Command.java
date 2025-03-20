package V1.ingsoft.controller.commands;

public interface Command {
    /**
     * Esegue il comando dato l'array delle options e l'array degli argomenti.
     *
     * @param options le options (senza il trattino iniziale)
     * @param args    gli argomenti
     */
    void execute(String[] options, String[] args);

    boolean canBeExecutedBy(int userPriority);

    boolean hasBeenExecuted();
}