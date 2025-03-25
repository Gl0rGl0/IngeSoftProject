package V4.Ingsoft.controller.commands;

public interface ListInterface {
    String getHelpMessage(int userPriority);

    String getInfo();

    boolean canBeExecutedBy(int userPriority);
}
