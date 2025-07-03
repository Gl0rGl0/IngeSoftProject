package V3.Ingsoft.controller.item.persone;

public enum PersonaType {
    // Priorities all in one place in case other person types are added...
    MAX("", Persona.class, 100),
    CONFIGURATORE("configuratori", Configuratore.class, 4),
    FRUITORE("fruitori", Fruitore.class, 2),
    VOLONTARIO("volontari", Volontario.class, 3),
    CAMBIOPSW("", Persona.class, 1),
    GUEST("guest", Persona.class, 0),
    ERROR("error", Persona.class, 0);

    private final int priority;
    private final String filePath;
    private final Class<? extends Persona> personaClass;

    PersonaType(String filePath, Class<? extends Persona> personaClass, int priority) {
        this.filePath = filePath;
        this.personaClass = personaClass;
        this.priority = priority;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Class<? extends Persona> getPersonaClass() {
        return this.personaClass;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return this.filePath;
    }
}
