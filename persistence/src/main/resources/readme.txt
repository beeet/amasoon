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
Diese Beziehungen hätte man auch mittels Embedded Objects lösen können. Wir
haben dies aber bewusst über eine @OneToOne Beziehung gelöst, damit separate
Tabellen für Address und CreditCard erstellt werden.
Der CascadeType wurde auf ALL gesetzt, sodass Adressen und Kreditkarten gelöscht
werden, sobald ein Customer oder eine Order gelöscht wird.

- Order --> LineItems
Werden bewusst EAGER geladen, da im Normalfall auf die LineItems auch noch
zugegriffen wird.

- Customer löschen
Wenn ein Customer gelöscht werden soll, hätte man ein Problem mit bestehenden
Orders. Daher haben wir das Modell erweitert, sodass Customers lediglich als
gelöscht markiert werden können.
