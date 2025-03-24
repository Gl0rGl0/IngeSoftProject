package V1.Ingsoft.Controller.commands;

public abstract class AbstractCommand implements Command {
    protected ListInterface commandInfo;
    protected boolean hasBeenExecuted = true; // di default, ogni comando lo segno come già eseguito, così il controllo
                                              // in 'and'
    // sull'esecuzione di tutti i comandi di setup va a buon fine
    // al momento di creazione dei comandi ADD, SETMAX, SETAMBITO, LOGINSETUP,
    // CHANGEPSW e DONE lo imposto a false, solo per questi però
    // vivere la vita è un gioco da ragazzi, me lo diceva mamma e io cadevo giù
    // dagli alberi

    @Override
    public boolean canBeExecutedBy(int userPriority) {
        return commandInfo.canBeExecutedBy(userPriority);
    }

    @Override
    public boolean hasBeenExecuted() {
        return hasBeenExecuted; // COSI QUELLI CHE VENGONO IMPLEMENTATI IN SETUP POSSONO MODIFICARLO
    }
}