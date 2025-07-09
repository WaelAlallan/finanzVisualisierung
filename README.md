<div align="center">
  
# FinanzVisualisierung

<h3><em>Transform Data Into Clear Financial Insights Instantly</em></h3>

<p align="center">
  <strong>A comprehensive Spring Boot application for financial data processing, visualization, and analytics</strong>
 
  ![Build](https://github.com/WaelAlallan/finanzVisualisierung/actions/workflows/maven.yml/badge.svg)
  ![License](https://img.shields.io/github/license/WaelAlallan/finanzVisualisierung)
  ![Last Commit](https://img.shields.io/github/last-commit/WaelAlallan/finanzVisualisierung)
  ![Repo Size](https://img.shields.io/github/repo-size/WaelAlallan/finanzVisualisierung)
  ![Stars](https://img.shields.io/github/stars/WaelAlallan/finanzVisualisierung?style=social)
  
</p>

### 🛠️ Built with the tools and technologies:

<p align="center">
  <a >
    <img src="https://skillicons.dev/icons?i=java,spring,js,html,css,maven,mysql,git" />
    <img src="https://www.thymeleaf.org/images/thymeleaf.png" alt="Thymeleaf" width="48"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/intellij/intellij-original.svg" height="48" alt="intellij logo"  />

  </a>
  <a>
  </a>
</p>

</div>

---


## Inhaltsverzeichnis
- [Überblick](#überblick)
- [Erste Schritte](#erste-schritte)
  - [Voraussetzungen](#voraussetzungen)
  - [Installation & Verwendung](#Installation&Verwendung)
  - [Verwendung](#verwendung)
  - [Tests](#tests)

---

## Überblick

FinanzVisualisierung ist eine robuste WebApp, die den Import und Visualisierung von Finanzdaten von Automobilunternehmen in einer skalierbaren Spring-Anwendung ermöglicht. Es unterstützt automatisierte Batch-Workflows und Batch-Jobs, interaktive Dashboards und umfassendes Datenmanagement und ist damit hilfreich für Finanzanalysen und Berichterstattung. (s. `src/main/resources/screenshots`)

### Was macht FinanzVisualisierung?

Dieses Spring-Projekt optimiert Finanzdaten-Workflows durch die Integration von Batch-Verarbeitung und webbasierter Visualisierung. Die Kernfunktionen umfassen:

- **Modulare Architektur**: Unterstützt skalierbare und wartbare Finanzanalyse-Workflows.

- **Automatisierte Batch-Verarbeitung**: Auslesen großer CSV-Datensätze für Unternehmen und dessen Finanzkennzahlen, und die DB aufnehmen.

- **Interaktive Dashboards**: Unternehmenskosten, Umsätze, Gewinne und Budget-Tracking werden mit dynamischen Diagrammen und Tabellen visualisiert. Man kann zwischen den Unternehmen wechseln und ein bestimmtes Jahr auswählen, um entsprechende Finanzzahlen anzuzeigen, zu analysieren und Entwicklungen zu erkennen. Dazu kann man die Zahlen auch in eine PDF-/Excel-Datei exportieren.


- **Robustes Speichermanagement**: Gewährleistet sichere, konfigurierbare Dateiverarbeitung mit benutzerdefinierter Fehlerbehandlung.

- **Automatisierte Workflows**: Bei CSV-Datei-Uploads wird die Datenverarbeitung automatisch ausgelöst und die Daten werden in die DB gespeichert. Somit werden manuelle Eingriffe reduziert.

- **Zuverlässigkeit & Erweiterbarkeit**: Entwickelt mit Wartbarkeit und zukünftigem Wachstum im Blick.

---

## Erste Schritte

### Voraussetzungen

Dieses Projekt erfordert die folgenden Abhängigkeiten:

- **Java JDK 21**
- **DB (MySQL, H2, ...)**
- **SpringBoot/Batch**
- **Maven**
- **Thymeleaf**
- **Lombok**
- **weitere Abhängigkeiten (s. ```pom.xml```)**


### Installation & Verwendung

Die Anwendung können Sie mit folgendem Befehl über das CLI laufen lassen:

```maven
mvn spring-boot:run
```

Nach der Installation können Sie die Anwendung unter `http://localhost:8080/index` aufrufen. Die Anwendung bietet:

- **Dashboard-Interface**: Anzeige von Finanz-charts, Kostentabellen, Budget Tracking und Analysen
- **Daten-Upload**: Import von CSV-Dateien für automatische Batch-Verarbeitung
- **Berichtsgenerierung**: Erstellung von Finanzberichte (PDF, Excel)
- **API-Endpunkte**: RESTful Services für Datenintegration


### Tests

Führen Sie die Test-Suite aus, um sicherzustellen, dass alles korrekt funktioniert:
```bash
mvn test
```

Für Integrationstests:
```bash
mvn verify
```

---

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert - siehe die [LICENSE](LICENSE) Datei für Details.

## Support

Wenn Sie auf Probleme stoßen oder Fragen haben, öffnen Sie bitte ein [Issue](https://github.com/WaelAlallan/finanzvisualisierung/issues) auf GitHub.

---
