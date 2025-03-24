package V1.Ingsoft.controller.commands.running;

import V1.Ingsoft.controller.commands.ListInterface;
import V1.Ingsoft.controller.item.persone.PersonaType;
import V1.Ingsoft.util.ConstString;

public enum CommandList implements ListInterface {
        ADD(ConstString.RUN_ADD_INFO, ConstString.RUN_ADD_LINE_INFO,
                        PersonaType.CONFIGURATORE.getPriority(),
                        PersonaType.CONFIGURATORE.getPriority()),

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

        LIST(ConstString.LIST_INFO, ConstString.LIST_LINE_INFO,
                        PersonaType.CONFIGURATORE.getPriority(),
                        PersonaType.MAX.getPriority()),

        PRECLUDE(ConstString.PRECLUDE_INFO, ConstString.PRECLUDE_LINE_INFO,
                        PersonaType.CONFIGURATORE.getPriority(),
                        PersonaType.MAX.getPriority()),

        MYVISIT(ConstString.MYVISIT_INFO, ConstString.MYVISIT_LINE_INFO,
                        PersonaType.FRUITORE.getPriority(),
                        PersonaType.VOLONTARIO.getPriority()),
        VISIT(ConstString.VISIT_INFO, ConstString.VISIT_LINE_INFO,
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
                for (CommandList element : CommandList.values()) {
                        if (element.canBeExecutedBy(userPriority)) {
                                out.append(element.name()).append(" ").append(element.lineInfo).append("\n");
                        }
                }
                return out.toString().substring(0, out.length() - 2); // rimuove l'ultimo "\n"
        }

        private final String message;
        private final String lineInfo;
        private final int minRequiredPermission; // livello minimo richiesto
        private final int maxRequiredPermission; // livello massimo richiesto

        CommandList(String message, String lineInfo, int minRequiredPermission, int maxRequiredPermission) {
                this.message = message;
                this.lineInfo = lineInfo;
                this.minRequiredPermission = minRequiredPermission;
                this.maxRequiredPermission = maxRequiredPermission;
        }

        @Override
        public String toString() {
                return this.lineInfo + "\n" + this.message;
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
