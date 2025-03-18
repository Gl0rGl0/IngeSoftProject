package V1.ingsoft.commands.setup;

import V1.ingsoft.commands.ListInterface;
import V1.ingsoft.persone.PersonaType;
import V1.ingsoft.util.ConstString;

public enum CommandListSETUP implements ListInterface {
    ADD(ConstString.SETUP_ADD_INFO, ConstString.SETUP_ADD_LINE_INFO,
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

    SETMAX(ConstString.SETMAX_INFO, ConstString.SETMAX_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    SETAMBITO(ConstString.SETAMBITO_INFO, ConstString.SETAMBITO_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    ASSIGN(ConstString.ASSIGN_INFO, ConstString.ASSIGN_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    DONE(ConstString.DONE_INFO, ConstString.DONE_LINE_INFO,
            PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    EXIT(ConstString.EXIT_INFO, ConstString.EXIT_LINE_INFO,
            PersonaType.GUEST.getPriority(),
            PersonaType.MAX.getPriority()),

    HELP(ConstString.HELP_INFO, ConstString.HELP_LINE_INFO,
            PersonaType.GUEST.getPriority(),
            PersonaType.MAX.getPriority());

    @Override
    public String getHelpMessage(int userPriority) {
        StringBuilder out = new StringBuilder();
        for (CommandListSETUP element : CommandListSETUP.values()) {
            if (element != HELP && canBeExecutedBy(userPriority)) {
                out.append(element.name()).append(" ").append(element.lineInfo).append("\n");
            }
        }
        return out.toString().substring(0, out.length() - 2); // rimuove l'ultimo "\n"
    }

    private final String message;
    private final String lineInfo;
    private final int minRequiredPermission; // livello minimo richiesto
    private final int maxRequiredPermission; // livello massimo richiesto

    CommandListSETUP(String message, String lineInfo, int minRequiredPermission, int maxRequiredPermission) {
        this.message = message;
        this.lineInfo = lineInfo;
        this.minRequiredPermission = minRequiredPermission;
        this.maxRequiredPermission = maxRequiredPermission;
    }

    @Override
    public String toString() {
        return this == HELP ? getHelpMessage(minRequiredPermission) : (this.lineInfo + "\n" + this.message);
    }

    @Override
    public String getInfo() {
        return this.lineInfo;
    }

    @Override
    public boolean canBeExecutedBy(int userPriority) {
        return minRequiredPermission <= userPriority && userPriority <= maxRequiredPermission;
    }
}
