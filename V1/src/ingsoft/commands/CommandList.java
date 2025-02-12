package ingsoft.commands;

import ingsoft.persone.PersonaType;

public enum CommandList {
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
        "Aggiunge una Persona/Visita/Luogo al database", PersonaType.CONFIGURATORE.getPriorita()),  // Solo i configuratori (4)

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
        "Rimuove una Persona/Visita/Luogo dal database", PersonaType.CONFIGURATORE.getPriorita()),  // Solo i configuratori (4)

    LOGIN("""
        login [String: username] [String: password]
            username  Specifica l'username con cui fare il login
            password  Specifica la password con cui fare il login
    """,
        "Esegui il login immettendo le credenziali", PersonaType.GUEST.getPriorita()),  // Tutti possono usarlo se non loggati(0)

    LOGOUT("""
        logout
    """,
        "Esegui il logout dal sistema", PersonaType.VOLONTARIO.getPriorita()),  // Devi essere almeno loggato (2)

    CHANGEPSW("""
        changepsw [String: nuovapsw]
            nuovapsw  Specifica la nuova password per l'account
            """, "Cambia la password", PersonaType.CAMBIOPSW.getPriorita()), // Devi essere almeno loggato ma devi cambiare psw perche GUEST non puo... (1)
    
    EXIT("""
        exit
    """,
        "Chiude il programma", PersonaType.GUEST.getPriorita()),  // Tutti possono usarlo (0)

    HELP("Questa lista", "Fornisce informazioni sui comandi disponibili.", PersonaType.GUEST.getPriorita());
        
    public String getHelpMessage(int permission) {
        StringBuilder out = new StringBuilder();
        for (CommandList element : CommandList.values()) {
            if (element != HELP && permission >= element.requiredPermission) {
                out.append(element.name()).append(" ").append(element.lineInfo).append("\n");
            }
        }
        return out.toString().substring(0, out.length() - 2); //toglie l'ultimo "\n" cosi da non avere troppi spazi ma non appesantendo la logica del for
    }
        
    private final String message;
    private final String lineInfo;
    private final int requiredPermission;  // livello minimo richiesto

    CommandList(String message, String lineInfo, int requiredPermission) {
        this.message = message;
        this.lineInfo = lineInfo;
        this.requiredPermission = requiredPermission;
    }

    @Override
    public String toString() {
        return this == HELP ? getHelpMessage(requiredPermission) : (this.lineInfo + "\n" + this.message);
    }

    public String getInfo(){
        return this.lineInfo;
    }

    public int getRequiredPermission() {
        return this.requiredPermission;
    }
}