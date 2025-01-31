package ingsoft.util;

public enum FunctionList {
    ADD("""
        add [-c] [-f] [-v] [String: username] [String: psw]
            -c        Aggiunge un configuratore
            -f        Aggiunge un fruitore
            -v        Aggiunge un volontario
            username  Specifica l'username della persona inserita
            psw       Specifica la password della persona inserita

        add [-V] [String: titolo] [String: descrizione] [GPS: puntoIncontro] [Date: dataInizioPeriodo] [Date: dataFinePeriodo] [Time: oraInizio] [int: durataVisita] [boolean: free] [int: numMinPartecipants] [int: numMaxPartecipants] [StatusLuoghi: stato]
            -V                 Aggiunge una visita
            titolo              Il titolo della visita
            descrizione         Una breve descrizione della visita
            puntoIncontro       Il punto di incontro per la visita [latitudine,longitudine]
            dataInizioPeriodo   Data di inizio del periodo della visita [YYYY-MM-DD]
            dataFinePeriodo     Data di fine del periodo della visita [YYYY-MM-DD]
            oraInizio           L'orario di inizio della visita [HH:MM]
            durataVisita        Durata della visita in minuti
            free                Indica se la visita è gratuita [true/false]
            numMinPartecipants  Numero minimo di partecipanti
            numMaxPartecipants  Numero massimo di partecipanti
            stato               Stato della visita [PROPOSTA/CONFERMATA/CANCELLATA/EFFETTUATA]

        add [-L] [String: nomeLuogo] [String: descrizioneLuogo] [GPS: posizione] [List<Integer>: visite]
            -L                 Aggiunge un luogo
            nomeLuogo          Nome del luogo
            descrizioneLuogo   Breve descrizione del luogo
            posizione          Posizione GPS [latitudine,longitudine]
            visite             Lista degli ID delle visite associate [id1,id2,...]
    """,
        "Aggiunge una Persona/Visita/Luogo al database"),

    REMOVE("""
        remove [-c] [-f] [-v] [String: username]
            -c        Rimuove un configuratore
            -f        Rimuove un fruitore
            -v        Rimuove un volontario
            username  Specifica l'username della persona da rimuovere
        
        remove [-V] [-L] [String: titolo]
            -V        Rimuove una visita
            -L        Rimuove un luogo
            titolo    Specifica il titolo della visita o del luogo da rimuovere
    """,
        "Rimuove una Persona/Visita/Luogo dal database"),

    LOGIN("""
        login [String: username] [String: password]
            username  Specifica l'username con cui fare il login
            password  Specifica la password con cui fare il login
    """,
        "Esegui il login immettendo le credenziali"),

    HELP(null, "Fornisce informazioni sui comandi disponibili.");

    static {
        HELP.message = getHelpMessage();
    }

    private static String getHelpMessage() {
        StringBuilder out = new StringBuilder();
        for (FunctionList element : FunctionList.values()) {
            if (element != HELP) {
                out.append(element.name()).append(" ").append(element.lineInfo).append("\n");
            }
        }
        return out.toString();
    }

    private String message;
    private final String lineInfo;

    FunctionList(String message, String lineInfo) {
        this.message = message;
        this.lineInfo = lineInfo;
    }

    @Override
    public String toString() {
        return this == HELP ? this.message : (this.lineInfo + "\n" + this.message);
    }


    public String getInfo(){
        return this.lineInfo;
    }
}
