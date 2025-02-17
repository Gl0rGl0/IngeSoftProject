package ingsoft.commands.setup;

import ingsoft.commands.ListInterface;
import ingsoft.persone.PersonaType;

public enum CommandListSETUP implements ListInterface{
    ADD("""
        add [-L] [String: nomeLuogo] [String: descrizioneLuogo] [GPS: posizione] [List<Integer>: visite]
            -L                 Aggiunge un luogo
            nomeLuogo          Nome del luogo
            descrizioneLuogo   Breve descrizione del luogo
            posizione          Posizione GPS [latitudine,longitudine]
            visite             Lista degli ID delle visite associate [id1,id2,...]
    """,
        "Aggiunge un Luogo al database", PersonaType.CONFIGURATORE.getPriorita(), PersonaType.CONFIGURATORE.getPriorita()),  // Solo i configuratori (4)

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
        "Gestione della data del sistema", PersonaType.GUEST.getPriorita(), PersonaType.MAX.getPriorita()), //TUTTI ma solo in demo (tempo virtuale)... (0,100)

    SETMAX("""
        setmax [int: max]
            max     Specifica il numero massimo di fruitori per una visita
    """, "(SETUP) Assegna il valore massimo delle visite", PersonaType.CONFIGURATORE.getPriorita(), PersonaType.CONFIGURATORE.getPriorita()),

    EXIT("""
        exit
    """,
        "Chiude il programma", PersonaType.GUEST.getPriorita(), PersonaType.MAX.getPriorita()),  // TUTTI (0,100)

    HELP("Questa lista", "Fornisce informazioni sui comandi disponibili.", PersonaType.GUEST.getPriorita(), PersonaType.MAX.getPriorita()); //TUTTI (0,100)
        
    public String getHelpMessage(int userPerm) {
        StringBuilder out = new StringBuilder();
        for (CommandListSETUP element : CommandListSETUP.values()) {
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

    CommandListSETUP(String message, String lineInfo, int minRequiredPermission, int maxRequiredPermission) {
        this.message = message;
        this.lineInfo = lineInfo;
        this.minRequiredPermission = minRequiredPermission;
        this.maxRequiredPermission = maxRequiredPermission;
    }

    // CommandListSETUP(CommandList l){
    //     this.message = 
    // }

    @Override
    public String toString() {
        return this == HELP ? getHelpMessage(minRequiredPermission) : (this.lineInfo + "\n" + this.message);
    }

    public String getInfo(){
        return this.lineInfo;
    }

    public boolean canPermission(int userPerm){
        return minRequiredPermission <= userPerm && userPerm <= maxRequiredPermission;
    }
}