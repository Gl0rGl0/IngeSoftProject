package ingsoft.persone;

public enum PersonaType {
        CONFIGURATORE("configuratori", Configuratore.class),
        FRUITORE("fruitori", Fruitore.class),
        VOLONTARIO("volontari", Volontario.class);

        private final String filePath;
        private final Class<? extends Persona> personaClass;

        PersonaType(String filePath, Class<? extends Persona> personaClass) {
                this.filePath = filePath;
                this.personaClass = personaClass;
        }

        public String getFilePath() {
                return filePath;
        }

        public Class<? extends Persona> getPersonaClass() {
                return personaClass;
        }
}