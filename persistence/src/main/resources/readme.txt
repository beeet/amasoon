Amasoon Persistence - Zusatzinformationen
=========================================

- BaseEntity
Damit auf sämtlichen Entities eine Id und optimistisches Locking vorhanden ist,
haben wir eine BaseEntity erstellt. Für die Version gibt es bewusst keine
Setter- und Getter-Methoden. Es gibt lediglich den Getter für die Id.

- Customer / BaseEntity
Primärschlüssel ist id. Damit die EmailAdresse eindeutig ist, haben wir ein
Unique-Constraint konfiguriert.

- Order/Customer --> Address/CreditCard
Zuerst haben wir bei diesem Punkt folgendes notiert:
  "Diese Beziehungen hätte man auch mittels Embedded Objects lösen können. Wir
   haben dies aber bewusst über eine @OneToOne Beziehung gelöst, damit separate
   Tabellen für Address und CreditCard erstellt werden.
   Der CascadeType wurde auf ALL gesetzt, sodass Adressen und Kreditkarten gelöscht
   werden, sobald ein Customer oder eine Order gelöscht wird."
Da bei der Anpassung des Klassendiagramms (18.12.2013) explizit darauf hingewiesen
wurde, dass es sich um Kompositionen handelt, haben wir uns dennoch für Embedded
Objects entschieden. Laut unserem Buch (Pro JPA 2) scheint dies hierfür auch
der richtige Ansatz zu sein: siehe p. 105 oben.

- Order --> LineItems
Werden bewusst EAGER geladen, da im Normalfall auf die LineItems auch noch
zugegriffen wird.

- Customer löschen
Wenn ein Customer gelöscht werden soll, hätte man ein Problem mit bestehenden
Orders. Daher haben wir das Modell erweitert, sodass Customers lediglich als
gelöscht markiert werden können.

- Query: "all books which contain a specific keywords in the title, authors or publisher field"
In der Aufgabenstellung wurde dieses Query noch abgeschwächt. Wir hatten die
schwierigere Variante bereits mit Hilfe der Criteria API umgesetzt (siehe
BookQueries.java).
Den Downgrade auf die einfachere Variante behalten wir uns vor! :-)

