package V1.ingsoft.commands;

public interface ListInterface {
    String getHelpMessage(int userPerm);

    String getInfo();

    boolean canPermission(int userPerm);
}
