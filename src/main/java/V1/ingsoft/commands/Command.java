package V1.ingsoft.commands;

public interface Command {
    /**
     * Esegue il comando dato l'array delle opzioni e l'array degli argomenti.
     *
     * @param options le opzioni (senza il trattino iniziale)
     * @param args    gli argomenti
     */
    void execute(String[] options, String[] args);

    boolean canBeExecutedBy(int userPriority);

    boolean hasBeenExecuted();
}