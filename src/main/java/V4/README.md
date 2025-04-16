# Sistema di Gestione Visite Guidate

## Descrizione del Progetto

Il presente progetto è un sistema software finalizzato a far incontrare la domanda e l’offerta di visite guidate
relative a luoghi caratterizzati da bellezze naturali, paesaggistiche e architettoniche, nonché da interesse storico,
artistico e culturale. L'applicazione è concepita per essere adottata da organizzazioni locali, prive di scopo di lucro,
che desiderano promuovere la conoscenza del territorio, avvalendosi di volontari che prestano la loro opera a title
gratuito.

## Funzionalità

- **Processo Incrementale/Iterativo:**  
  Lo sviluppo del sistema avviene in fasi, garantendo un continuo miglioramento e integrazione di nuove funzionalità.

- **Gestione dei Tipi di Utente:**
    - **Configuratore:**  
      È l'esponente dell'organizzazione incaricato della pianificazione delle visite guidate. Utilizza l'applicazione
      per inserire, aggiornare e consultare i dati relativi alle visite e alle guide volontarie accreditate.

    - **Volontario:**  
      È la guida accreditata dall'organizzazione. Il volontario dichiara periodicamente la propria disponibilità e
      consulta l'elenco delle visite assegnategli.

    - **Fruitore:**  
      È l'utente finale che si rivolge all'applicazione con l'intento di partecipare a una visita guidata. Il front-end
      mostra ai fruitori le visite proposte, raccoglie le iscrizioni e, al termine del periodo di registrazione,
      comunica se la visita è stata confermata o cancellata.

- **Architettura del Sistema:**
    - **Back-end:**  
      Interagisce con il configuratore e il volontario, raccogliendo dati relativi alle visite, alle guide e alle
      disponibilità.

    - **Front-end:**  
      È destinato all'interazione con i fruitori, consentendo la consultazione delle visite e la gestione delle
      iscrizioni.

- **Definizione dell’Ambito Territoriale:**  
  L'applicazione è progettata per operare in un'area territoriale ristretta (ad esempio, un singolo comune o un insieme
  di comuni limitrofi). Tale ambito viene definito una volta sola, al momento dell'inizializzazione da parte di un
  configuratore, e rimane costante per tutti gli utenti.

- **Persistenza dei Dati:**  
  Tutti i dati raccolti (visite, disponibilità dei volontari, iscrizioni dei fruitori) vengono salvati in forma
  persistente, garantendo la continuità del servizio.

## Struttura del Progetto

- **src:**  
  Cartella contenente i sorgenti del progetto.

- **lib:**  
  Cartella destinata alle dipendenze.

- **bin:**  
  Cartella dove vengono generati i file compilati.

## Gestione delle Dipendenze

Le dipendenze del progetto possono essere gestite tramite il pannello `JAVA PROJECTS` di Visual Studio Code. Per
ulteriori dettagli, consulta
la [documentazione ufficiale](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Istruzioni d'Uso

L'applicazione si compone di due interfacce principali:

- **Interfaccia Back-end:**  
  Utilizzata dai configuratori e dai volontari per la gestione dei dati relativi alle visite e alle disponibilità.

- **Interfaccia Front-end:**  
  Rivolta ai fruitori, permette di consultare le visite proposte, effettuare iscrizioni e visualizzare lo stato della
  conferma o cancellazione delle visite.

## Autori

- Giorgio Felappi
- Daniel Barbetti

## Licenza

Licenza mia e sua.