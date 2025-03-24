package V1.Ingsoft.Controller.commands;

public interface ListInterface {
    String getHelpMessage(int userPriority);

    String getInfo();

    boolean canBeExecutedBy(int userPriority);
}
