# Prototyp Occupi
PM3-HS25-IT24a_ZH-Team4<br>
10.11.2025

## Inhaltsverzeichnis
1. [Einführung](#einführung)
2. [Installation](#installation)
3. [Verwendung](#verwendung)
4. [Architektur](#architektur)
5. [Pakete](#pakete)
6. [Tests](#tests)
7. [Bekannte Einschränkungen](#bekannte-einschränkungen)
8. [Team](#team)
9. [Lizenz](#lizenz)

---

## Einführung

### Ausgangslage
An der ZHAW kommt es insbesondere zu Stosszeiten häufig zu einer ungleichen Auslastung der Räume. Das bestehende Raumreservationssystem erlaubt Mitarbeitenden zwar Vorab-Buchungen, liefert aber keine Informationen über die aktuelle Belegung oder Ausstattung. Studierende haben keinen Zugang zu diesem System und müssen aufwendig nach freien Arbeitsplätzen suchen, was zu ineffizienter Ressourcennutzung führt.

### Die Lösung: Occupi
**Occupi** ist ein Prototyp zur Anzeige und Suche von freien Räumen und Arbeitsplätzen an der ZHAW. Die Software stellt Raumdaten inkl. Ausstattung dar, erlaubt Filter nach Standort/Gebäude/Stockwerk/Typ/Kapazität/Ausstattung und zeigt (simulierte) Belegungs- und Event-Informationen.

Hinweis: Der aktuelle Stand arbeitet mit JSON-basierten Beispieldaten (keine Live-Anbindung an ZHAW-Systeme).

#### Kernfunktionen (aktueller Stand)
- **Suche und Filterung**: Umfassende Suchfunktion mit mehreren Filterkriterien
    - Standort, Gebäude, Stockwerk, Raumtyp, Kapazität, Infrastruktur
    - Paginierte Ergebnisanzeige für bessere Performance bei vielen Treffern

- **Belegungsanzeige**: Darstellung von Raum-Status aus simulierten Daten
    - Status-Stufen: FREE, RESERVED, OCCUPIED, EVENT, FULL
    - Integration von Sensor- und Reservationsdaten (via APIs)

- **Raumdetails**: Detailansicht mit umfassenden Informationen
    - Ausstattung: WLAN-Qualität, Steckdosen, Tafeln/Beamer, Klimaanlage
    - Aktuelle Events und Belegung
    - Kapazität und Barrierefreiheit

- **Event-Verwaltung**: Erstellen und Anzeigen von Lern-Events
    - Dialog zum Erstellen neuer Events mit Titel, Beschreibung, Zeitraum
    - Tagging-System für Events
    - Event-Anzeige pro Raum

- **Daten-Persistenz**:
    - JSON-basierte Speicherung von Gebäudedaten
    - Zentrale JSON-Serialisierung über `JsonReader<T>`
    - Validierung von Dateipfaden mit `FileValidator`

- **Analyse-Komponenten** (implementiert, UI-Integration ausstehend):
    - `RoomUsageAnalyzer`: Auswertung historischer Raumbelegungsdaten
    - Heatmap-Visualisierung mit `MatrixHeatMap`
    - Berechnung des am wenigsten genutzten Wochentags

---

## Installation

### Voraussetzungen
- Java Development Kit (JDK) 21 oder höher
- Gradle (wird über Gradle Wrapper bereitgestellt)

### Installation und Build
1. Repository klonen oder Projektordner öffnen
2. Abhängigkeiten installieren und Projekt bauen:

   macOS/Linux:
   ```bash
   ./gradlew build
   ```
   Windows (cmd):
   ```bat
   gradlew.bat build
   ```

### Hauptabhängigkeiten
- JavaFX: GUI-Framework
- Jackson: JSON-Serialisierung/-Deserialisierung (inkl. jsr310 für Java Time)
- Ikonli: Icon-Bibliothek (Bootstrap Icons)
- JUnit Jupiter: Unit-Testing-Framework
- Mockito: Mocking-Framework für Tests

---

## Verwendung

### Anwendung starten
macOS/Linux:
```bash
./gradlew run
```
Windows (cmd):
```bat
gradlew.bat run
```

### Distribution erstellen
Eine verteilbare Version der Anwendung kann wie folgt erstellt werden:

macOS/Linux:
```bash
./gradlew installDist
```
Windows (cmd):
```bat
gradlew.bat installDist
```
Die Distribution befindet sich anschliessend in `build/install/Occupi/`.

### Tests ausführen
Alle Tests ausführen:

macOS/Linux:
```bash
./gradlew test
```
Windows (cmd):
```bat
gradlew.bat test
```

Testreports befinden sich nach Ausführung in `build/reports/tests/test/index.html`.

---

## Architektur

### Logische Architektur
Die Anwendung folgt einer mehrschichtigen Struktur mit klarer Trennung der Verantwortlichkeiten:

- **Benutzerschnittstelle (UI)**: JavaFX-basierte GUI
    - FXML-Layout (`MainWindow.fxml`)
    - Custom Controls (`RoomResultSection`, `RoomDetailView`, `EventTab`, `MatrixHeatMap`)
    - CSS-Styling (`styles.css`)

- **Anwendungslogik (Controller)**: Orchestrierung und Koordination
    - `MainWindowController`: UI-Interaktion, Filter, Suche
    - `BuildingController`: Gebäudedatenverwaltung, Koordination von Storage/Sensor/Reservation-APIs

- **Fachlogik (Domain/Model)**: Kernentitäten und Business-Logik
    - Domänenmodelle: `Building`, `BuildingInfo`, `Room`, `RoomEquipment`, `Event`
    - Enumerationen: `RoomType`, `RoomState`, `RoomInfrastructure`
    - Such-/Filterlogik: `Search`, `FilterCriteria`, `SearchResult`

- **Datenhaltung (Storage & APIs)**: Persistenz und externe Schnittstellen
    - Storage: `BuildingStorageAPI` (Interface) + `FileBuildingStorageAPI` (JSON-basiert)
    - Reservations-API: `ReservationAPI` + `FileReservationAPI` (Simulation)
    - Sensor-API: `SensorAPI` + `FileSensorAPI` (Simulation)
    - History-API: `HistoryAPI` + `FileHistoryAPI` (Simulation)

- **Utilities**: Querschnittsfunktionalität
    - `JsonReader<T>`: Zentrale JSON-Serialisierung für alle File-APIs
    - `FileValidator`: Dateipfad-Validierung
    - `RoomUsageAnalyzer`: Analyse historischer Raumbelegungsdaten
    - `Weekdays`: Wochentags-Enum mit deutschen Labels

**Architektur-Prinzipien:**
- **Dependency Inversion**: Alle File-APIs hängen von `JsonReader` ab, nicht direkt von Jackson
- **Interface Segregation**: Klare API-Contracts (ReservationAPI, SensorAPI, HistoryAPI, BuildingStorageAPI)
- **Single Responsibility**: Jede Klasse hat eine klare, fokussierte Verantwortung
- **Separation of Concerns**: UI, Business-Logik, Persistenz sind klar getrennt

Detaillierte Beziehungen und Abhängigkeiten sind im [Klassendiagramm](doc/classdiagram.md) dargestellt.

---

## Pakete

Kurzüberblick aller relevanten Pakete und warum sie benötigt werden:

- ch.zhaw.it.pm3.occupi
  - Enthält den Einstiegspunkt `Occupi` (JavaFX Application). Lädt FXML/CSS, initialisiert Controller und startet die UI.
- ch.zhaw.it.pm3.occupi.controllers
  - UI-nahe Steuerung. `MainWindowController` orchestriert Filter, Suche und Ergebnisanzeige. `BuildingController` verwaltet Gebäudedaten (lädt via Storage) und delegiert Suchanfragen an die Suchlogik.
- ch.zhaw.it.pm3.occupi.model
  - Domänenmodell: `Building`, `BuildingInfo`, `Room`, `RoomEquipment`, `RoomInfrastructure` (Enum), `RoomType` (Enum), `RoomState` (Enum), `Event`. Grundlage für Suche, Anzeige und Validierung.
- ch.zhaw.it.pm3.occupi.search
  - Such-/Filterlogik: `FilterCriteria` beschreibt Filter; `Search` wendet sie auf Gebäudelisten an und liefert ein `SearchResult` (Gruppierung nach Gebäude). Zusätzlich generischer `JsonReader` für JSON-IO.
- ch.zhaw.it.pm3.occupi.storage
  - Persistenzschicht für Gebäude: `BuildingStorageAPI` und `FileBuildingStorageAPI` (liest/schreibt `BuildingData.json`). Benötigt, um Demodaten zu laden und ggf. zu speichern.
- ch.zhaw.it.pm3.occupi.reservation.api
  - Schnittstellen und DTOs für Reservationen (`ReservationAPI`, `RoomReservation`, `TimeWindow`). Legt das Integrations-Contract für künftige Anbindung fest.
- ch.zhaw.it.pm3.occupi.reservation.simulation
  - JSON-basierte Demo-Implementierung (`JsonReservationAPI`) liest `ReservationData.json`. Dient als Platzhalter statt einer echten Systemanbindung.
- ch.zhaw.it.pm3.occupi.sensors.api
  - Abstraktion für Sensordaten (`SensorAPI`, `RoomOccupancy`). Definiert, wie Belegungsdaten bezogen werden.
- ch.zhaw.it.pm3.occupi.sensors.simulation
  - JSON-basierte Demo-Implementierung (`JsonSensorAPI`) liest `SensorData.json`. Aktuell nicht in die UI integriert; vorbereitet für spätere Live-/Simulations-Anbindung.
- ch.zhaw.it.pm3.occupi.history
  - Grundgerüst für Nutzungsverlaufsdaten (`HistoryAPI`, `HistoryDatabaseFile`, `RoomHistory`). Noch nicht in die UI integriert; Basis für spätere Auswertungen.
- ch.zhaw.it.pm3.occupi.ui
  - UI-Hilfskomponenten für Ergebnislisten und Detailansichten (`RoomResultSection`, `RoomDetailView`, `EventTab`, `EventCreationDialog`). Ergänzt das FXML `MainWindow.fxml` in `resources`.

---

## Tests

### Testing-Strategie
Unit-Tests decken zentrale Domänenklassen (Room, Building, RoomEquipment, Event) und die Suchlogik ab. Mockito steht bereit für Mocks, JUnit Jupiter für Assertions/Testlauf.

Detaillierte Informationen zum Test-Konzept finden sich in der [Testing-Dokumentation](doc/testing-concept.md). Test-Cases sind je Klasse dokumentiert (siehe [Testing-Cases](doc/testcases)).

### Coverage Reports
Nach Testausführung stehen Reports zur Verfügung:
- HTML-Report: `build/reports/tests/test/index.html`
- XML-Report: `build/test-results/test/`

---

## Bekannte Einschränkungen
- Keine Live-Anbindung an ZHAW-Reservations- oder Sensorsysteme; stattdessen JSON-Simulationen.
- Die Sensor-/Reservations-APIs sind aktuell nicht in den `BuildingController`/die UI eingehängt.
- Events, die in der UI erstellt werden, werden nur im Speicher gehalten (keine Persistenz über App-Neustart hinaus).
- Einige UI-Werte sind Platzhalter (z. B. „Ruhefaktor“).

---

## Projektstruktur

```
occupi/
├── src/
│   ├── main/
│   │   ├── java/ch/zhaw/it/pm3/occupi/
│   │   │   ├── Occupi.java                    # Haupteinstiegspunkt (JavaFX Application)
│   │   │   ├── package-info.java              # Paket-Dokumentation
│   │   │   ├── controllers/                   # MainWindowController, BuildingController
│   │   │   ├── model/                         # Building, Room, Event, Enums (RoomType, RoomState, etc.)
│   │   │   ├── search/                        # FilterCriteria, Search, SearchResult, PaginatedSearchResult
│   │   │   ├── storage/                       # BuildingStorageAPI, FileBuildingStorageAPI
│   │   │   ├── reservation/
│   │   │   │   ├── api/                       # ReservationAPI, RoomReservation, TimeWindow
│   │   │   │   └── simulation/                # FileReservationAPI (JSON-basiert)
│   │   │   ├── sensors/
│   │   │   │   ├── api/                       # SensorAPI, RoomOccupancy
│   │   │   │   └── simulation/                # FileSensorAPI (JSON-basiert)
│   │   │   ├── history/
│   │   │   │   ├── api/                       # HistoryAPI
│   │   │   │   └── simulation/                # FileHistoryAPI, RoomHistory, UsageEntry
│   │   │   ├── util/                          # JsonReader, FileValidator, RoomUsageAnalyzer, Weekdays
│   │   │   └── ui/                            # RoomResultSection, RoomDetailView, EventTab, MatrixHeatMap, EventCreationDialog
│   │   └── resources/
│   │       ├── ch/zhaw/it/pm3/occupi/         # MainWindow.fxml
│   │       ├── css/                           # styles.css
│   │       └── jsonData/
│   │           ├── buildingData/              # BuildingData.json
│   │           ├── reservationData/           # ReservationData.json
│   │           └── sensorData/                # SensorData.json
│   └── test/
│       └── java/ch/zhaw/it/pm3/occupi/        # Unit-Tests (BuildingTest, RoomTest, SearchTest, etc.)
├── doc/
│   ├── classdiagram.md                        # UML-Klassendiagramm (Mermaid)
│   ├── classdiagram.png/svg                   # Visualisierung des Klassendiagramms
│   ├── testing-concept.md                     # Test-Konzept und -Strategie
│   └── testcases/                             # Detaillierte Testfall-Dokumentation pro Klasse
├── gradle/                                    # Gradle Wrapper Dateien
├── build.gradle.kts                           # Gradle Build-Konfiguration
├── gradle.properties                          # Gradle-Eigenschaften
├── gradlew / gradlew.bat                      # Gradle Wrapper Skripte
└── README.md                                  # Diese Datei
```

---

## Datenquellen

Der Prototyp verwendet JSON-basierte Datenquellen zur Simulation:
- `BuildingData.json`: Gebäudedaten, Räume, Ausstattung
- `ReservationData.json`: Raumreservierungen und Buchungsdaten (für Simulation)
- `SensorData.json`: Simulierte Sensordaten für Raumbelegung

Diese befinden sich in `src/main/resources/jsonData/`.

---

## Team

PM3-HS25-IT24a_ZH-Team4

Das Occupi-Projekt wurde von einem fünfköpfigen Team im Rahmen des Bachelor Studiengangs Informatik an der ZHAW entwickelt. Die Entwicklung erfolgte im dritten Semester als Teil der Module **PM3** (Projektmodul 3) und **SWEN1** (Software Engineering 1).

---

## Lizenz

Alle Rechte vorbehalten (2025). Dieses Projekt wird im Rahmen des Moduls PM3 an der ZHAW entwickelt und wird als Prototyp vorgestellt. Eine Freigabe des Source Codes bleibt den Team-Mitgliedern vorbehalten. 
