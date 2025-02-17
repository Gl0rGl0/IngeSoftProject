package ingsoft.commands.running;

import ingsoft.commands.ListInterface;
import ingsoft.persone.PersonaType;

public enum CommandList implements ListInterface{
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
        "Aggiunge una Persona/Visita/Luogo al database", PersonaType.CONFIGURATORE.getPriorita(), PersonaType.CONFIGURATORE.getPriorita()),  // Solo i configuratori (4)

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
        "Rimuove una Persona/Visita/Luogo dal database", PersonaType.CONFIGURATORE.getPriorita(), PersonaType.CONFIGURATORE.getPriorita()),  // Solo i configuratori (4)

    LOGIN("""
        login [String: username] [String: password]
            username  Specifica l'username con cui fare il login
            password  Specifica la password con cui fare il login
    """,
        "Esegui il login immettendo le credenziali", PersonaType.GUEST.getPriorita(), PersonaType.GUEST.getPriorita()),  // SOLO GUEST (0,0)

    LOGOUT("""
        logout
    """,
        "Esegui il logout dal sistema", PersonaType.CAMBIOPSW.getPriorita(), PersonaType.MAX.getPriorita()),  // Devi essere almeno nel sistema (1,100)

    CHANGEPSW("""
        changepsw [String: nuovapsw]
            nuovapsw  Specifica la nuova password per l'account
            """, "Cambia la password", PersonaType.CAMBIOPSW.getPriorita(), PersonaType.MAX.getPriorita()), // Devi essere almeno loggato ma devi cambiare psw perche GUEST non puo... (1,100)

    TIME("""
        time [[-d] [-m] [-a]] [int: giorni]
            giorni  Specifica il numero di giorni da saltare
            opzionalmente -d: numero di giorni
                          -m: numero di mesi
                          -a: numero di anni
        time -s [Date gg/mm/aa]
            Imposta la data attuale a gg/mm/aa
        time
            Mostra la data attuale
    """,
        "Gestione della data del sistema", PersonaType.GUEST.getPriorita(), PersonaType.MAX.getPriorita()), //TUTTI (0,100)

    LIST("""
        list [-l] [-v]
            -l(L):  Lista dei luoghi visitabili
            -v:     Lista dei volontari disponibili
        list [-V] [[-a] [-p] [-c] [-C] [-e]]
            -V: Lista delle visite disponibili
                Opzionalmente ([-p] default):   [-a]  All, tutte le visite (anche passate)
                                                [-p]  Lista delle visite Proposte
                                                [-c]  Lista delle visite Complete
                                                [-C]  Lista delle visite Cancellate
                                                [-e]  Lista delle visite Effettuate (passate)
    """,
        "Visualizza la lista desiderata", PersonaType.CONFIGURATORE.getPriorita(), PersonaType.MAX.getPriorita()), //TUTTI (0,100)ù

    PRECLUDE("""
            
            """, "Aggiunge/rimuove data da precludere", PersonaType.CONFIGURATORE.getPriorita(), PersonaType.MAX.getPriorita()),
    
    EXIT("""
        exit
    """,
        "Chiude il programma", PersonaType.GUEST.getPriorita(), PersonaType.MAX.getPriorita()),  // TUTTI (0,100)

    HELP("Questa lista", "Fornisce informazioni sui comandi disponibili.", PersonaType.GUEST.getPriorita(), PersonaType.MAX.getPriorita()); //TUTTI (0,100)
        
    @Override
    public String getHelpMessage(int userPerm) {
        StringBuilder out = new StringBuilder();
        for (CommandList element : CommandList.values()) {
            if (element != HELP && canPermission(userPerm)) {
                out.append(element.name()).append(" ").append(element.lineInfo).append("\n");
            }
        }
        return out.toString().substring(0, out.length() - 2); //toglie l'ultimo "\n" cosi da non avere troppi spazi ma non appesantendo la logica del for
    }
        
    private final String message;
    private final String lineInfo;
    private final int minRequiredPermission;  // livello minimo richiesto
    private final int maxRequiredPermission;  // livello massimo richiesto

    CommandList(String message, String lineInfo, int minRequiredPermission, int maxRequiredPermission) {
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
    public String getInfo(){
        return this.lineInfo;
    }

    @Override
    public boolean canPermission(int userPerm){
        return minRequiredPermission <= userPerm && userPerm <= maxRequiredPermission;
    }
}