package ingsoft.persone;

public enum PersonaType {
        //Priorita tutte in un unico posto in caso si aggiungano altre persone...
        CONFIGURATORE("configuratori", Configuratore.class, 4),
        FRUITORE("fruitori", Fruitore.class, 3),
        VOLONTARIO("volontari", Volontario.class, 2),
        CAMBIOPSW("nessuna", Persona.class, 1),
        GUEST("nessuna", Persona.class, 0),
        ERROR("error", Persona.class, 0);

        private final int priorita;
        private final String filePath;
        private final Class<? extends Persona> personaClass;

        PersonaType(String filePath, Class<? extends Persona> personaClass, int priorita) {
                this.filePath = filePath;
                this.personaClass = personaClass;
                this.priorita = priorita;
        }

        public String getFilePath() {
                return this.filePath;
        }

        public Class<? extends Persona> getPersonaClass() {
                return this.personaClass;
        }

        public int getPriorita(){
                return this.priorita;
        }

        @Override
        public String toString(){
                return this.filePath;
        }
}