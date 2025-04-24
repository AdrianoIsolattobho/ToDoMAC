# Documentazione “ToDo”

## Introduzione
Questo progetto è basato sullo sviluppo di un sistema informativo per gestire delle attività, ispirato al software **Trello**, sviluppato in **Java** con un’interfaccia grafica.

La documentazione comprende:
- Un’analisi del problema.
- Un’analisi delle classi con il relativo class diagram.
- L’organizzazione degli oggetti.
- La motivazione delle scelte architetturali prese durante la modellazione UML.

---

## Modello UML
Il modello UML è composto da:
- **Cinque classi principali**: `Utente`, `Bacheca`, `ToDo`, `Condivisione`, `Checklist`, `Attività`.
- **Due enumerazioni**: `Titolo`, `Ordinamento`.
- **Una classe associativa**: `Condivisione`.

Le relazioni tra le classi rappresentano le interazioni tra le varie entità:
- L’utente gestisce le bacheche e condivide i ToDo con altri utenti.
- I ToDo possono contenere una sola checklist.

---

## Dettaglio delle Classi

### Classe `UTENTE`
- **Attributi**:
  - `email`: Identifica univocamente l’utente.
  - `password`: Per l’autenticazione.
  - `bacheche`: Array di massimo tre bacheche.
- **Relazione**:
  - Un utente può gestire da 1 a 3 bacheche (`Università`, `Lavoro`, `Tempo Libero`).
  - La relazione è navigabile solo da utente a bacheca.
- **Metodi**:
  - Gestione delle bacheche e dei ToDo al loro interno.

---

### Classe `BACHECA`
- **Attributi**:
  - `titolo`: Enumerazione (`Università`, `Lavoro`, `Tempo Libero`).
  - `descrizione`: Descrizione della bacheca.
  - `ordinamento`: Enumerazione per definire l’ordinamento dei ToDo.
  - `toDoList`: Lista dinamica di ToDo.
- **Relazione**:
  - Aggregazione composta: una bacheca contiene i ToDo.
- **Metodi**:
  - `spostaToDo`: Sposta un ToDo da una bacheca all’altra.
  - `inScadenza`: Mostra i ToDo in scadenza oggi.
  - `inScadenzaSpecifico`: Mostra i ToDo in scadenza in una data specifica.
  - `ricerca(parametro)`: Ricerca un ToDo per nome o titolo.

---

### Classe `TODO`
- **Attributi**:
  - `titolo`: Titolo del ToDo.
  - `descrizione`: Descrizione del ToDo.
  - `link`: URL associato al ToDo.
  - `immagine`: Immagine associata.
  - `sfondo`: Colore di sfondo.
  - `dataCreazione`: Data di creazione (impostata automaticamente).
  - `dataScadenza`: Data di scadenza.
  - `completato`: Stato del ToDo (completato o meno).
  - `manuale`: Permette di spostare un ToDo manualmente.
  - `checklist`: Una checklist associata (cardinalità 0…1).
- **Metodi**:
  - `condividi()`: Condivide il ToDo con altri utenti.
  - `eliminaCondivisione()`: Rimuove la condivisione.

---

### Classe `CONDIVISIONE`
- **Attributi**:
  - `creatore`: Utente che ha creato la condivisione.
  - `condivisiCon`: Lista di utenti con cui è condiviso il ToDo.
  - `toDoCondiviso`: ToDo condiviso.
- **Relazione**:
  - Un ToDo può essere condiviso con più utenti.
  - Una condivisione può coinvolgere più utenti.

---

### Classe `CHECKLIST`
- **Attributi**:
  - `attività`: Lista di attività associate al ToDo.
- **Relazione**:
  - Associazione composta: una checklist è composta da attività.
- **Metodi**:
  - Gestione dinamica delle attività.

---

### Classe `ATTIVITÀ`
- **Attributi**:
  - `nome`: Nome dell’attività.
  - `completata`: Stato dell’attività (completata o meno).
- **Relazione**:
  - Ogni attività appartiene a una checklist.

---

## Note Finali
Questo progetto rappresenta un sistema informativo per la gestione delle attività, con un’architettura modulare e scalabile. Le scelte progettuali sono state guidate dalla necessità di mantenere il codice leggibile, riutilizzabile e facilmente estendibile.