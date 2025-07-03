package V5.Ingsoft.controller.commands.list;

import V5.Ingsoft.controller.item.interfaces.ListInterface;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.ConstString;

public enum CommandList implements ListInterface {
    //adder (luoghi,tipivisite,configuratori,volontari)
    ADD(ConstString.ADD_INFO, ConstString.ADD_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    //remover (luoghi,tipivisite,configuratori,volontari, fruitori)
    REMOVE(ConstString.REMOVE_INFO, ConstString.REMOVE_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    LOGIN(ConstString.LOGIN_INFO, ConstString.LOGIN_LINE_INFO,
            PersonaType.GUEST.getPriority(),
            PersonaType.GUEST.getPriority()),

    LOGOUT(ConstString.LOGOUT_INFO, ConstString.LOGOUT_LINE_INFO,
            PersonaType.CAMBIOPSW.getPriority(),
            PersonaType.MAX.getPriority()),

    CHANGEPSW(ConstString.CHANGEPSW_INFO, ConstString.CHANGEPSW_LINE_INFO,
            PersonaType.CAMBIOPSW.getPriority(),
            PersonaType.MAX.getPriority()),

    TIME(ConstString.TIME_INFO, ConstString.TIME_LINE_INFO,
            PersonaType.GUEST.getPriority(),
            PersonaType.MAX.getPriority()),

    ASSIGN(ConstString.ASSIGN_INFO, ConstString.ASSIGN_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    DISASSIGN(ConstString.DISASSIGN_INFO, ConstString.DISASSIGN_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    LIST(ConstString.LIST_INFO, ConstString.LIST_LINE_INFO,
            PersonaType.FRUITORE.getPriority(),
            PersonaType.MAX.getPriority()),

    AVAILABILITY(ConstString.AVAILABILITY_INFO, ConstString.AVAILABILITY_LINE_INFO,
            PersonaType.VOLONTARIO.getPriority(),
            PersonaType.VOLONTARIO.getPriority()),

    PRECLUDE(ConstString.PRECLUDE_INFO, ConstString.PRECLUDE_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.MAX.getPriority()),

    MYVISIT(ConstString.MYVISIT_INFO, ConstString.MYVISIT_LINE_INFO,
            PersonaType.FRUITORE.getPriority(),
            PersonaType.VOLONTARIO.getPriority()),

    MAKEPLAN(ConstString.MAKEPLAN_INFO, ConstString.MAKEPLAN_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    COLLECTIONMANAGER(ConstString.COLLECTIONMANAGER_INFO, ConstString.COLLECTIONMANAGER_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    VISIT(ConstString.VISIT_INFO, ConstString.VISIT_LINE_INFO,
            PersonaType.FRUITORE.getPriority(),
            PersonaType.FRUITORE.getPriority()),

    EXIT(ConstString.EXIT_INFO, ConstString.EXIT_LINE_INFO,
            PersonaType.GUEST.getPriority(),
            PersonaType.MAX.getPriority()),

    HELP(ConstString.HELP_INFO, ConstString.HELP_LINE_INFO,
            PersonaType.GUEST.getPriority(),
            PersonaType.MAX.getPriority());

    private final String message;
    private final String lineInfo;
    private final int minRequiredPermission; // minimum required level
    private final int maxRequiredPermission; // maximum required level

    CommandList(String message, String lineInfo, int minRequiredPermission, int maxRequiredPermission) {
        this.message = message;
        this.lineInfo = lineInfo;
        this.minRequiredPermission = minRequiredPermission;
        this.maxRequiredPermission = maxRequiredPermission;
    }

    @Override
    public String getHelpMessage(int userPriority) {
        StringBuilder out = new StringBuilder();
        for (CommandList element : CommandList.values()) {
            if (element.canBeExecutedBy(userPriority)) {
                out.append(element.name()).append(" ").append(element.lineInfo).append("\n");
            }
        }
        return out.substring(0, out.length() - 2); // removes the last "\n"
    }

    @Override
    public String toString() { return this.lineInfo + "\n" + this.message; }

    @Override
    public String getInfo() { return this.lineInfo; }

    @Override
    public boolean canBeExecutedBy(int userPriority) {
        return minRequiredPermission <= userPriority && userPriority <= maxRequiredPermission;
    }
}
