# sql2json

Der sql2json Transformator (Trafo) arbeitet pro Programmaufruf ein Json-Template mit n im Template enthaltenen 
Trafo-Tags ab. Für jedes Trafo-Tag setzt der Trafo ein Sql-Statement auf die Metadatenbank ab und 
ersetzt das Trafo-Tag mit dem Ergebnis des SQL-Queries.

## Dokumentationen

In den folgenden Kapiteln ist die Konfiguration und Benutzung des Trafo beschrieben. Die Entwicklerdoku ist
[hier](DevDok.md) zuhause.

## Downloaden und starten

Der Trafo ist ein ein executable fat jar, dessen Releases in diesem 
[Bintray Maven Package](https://dl.bintray.com/sogis/simi/ch/so/agi/sql2json/) abgelegt sind.

Befehl zur Ausgabe der Hilfe:
```shell script
java -jar sql2json.jar -h
```
Die Konfiguration des Trafo erfolgt mittels Kommandozeilenparameter und/oder Umgebungsvariable. Bei Variablen, welche 
sowohl auf Kommandozeile wie in Umgebungsvariable definiert sind, wird der Wert der Kommandozeile verwendet.

|Bezeichnung|Parameter|Umgebungsvariable|Bemerkung|
|---|---|---|---|
|Template-Pfad|-t|SqlTrafo_Templatepath|Absoluter Dateipfad zum zu verarbeitenden Template. Bsp: opt/user/trafo/wms/template.json|
|Pfad zu Output|-o|SqlTrafo_Outputpath|Absoluter pfad und Dateiname des output config.json. Bsp: opt/user/trafo/wms/config.json|
|DB-Connection|-c|SqlTrafo_DbConnection|JDBC Connection-URL zur abzufragenden DB. Aufbau: jdbc:postgresql://host:port/database|
|DB-User|-u|SqlTrafo_DbUser|Benutzername für die DB-Verbindung|
|DB-Password|-p|SqlTrafo_DbPassword|Passwort für die DB-Verbindung|

Siehe die Integrationstests als Beispiele:
* [exit_ok.sh](inttest/exit_ok.sh): Aufruf via Kommandozeilen-Parameter
* [env_ok.sh](inttest/env_ok.sh): Aufruf via Umgebungs-Variablen

### Logging

Trafo nutzt die Bibliothek slf4j-simple. Loglevel und Ausgabeformat können mittels Java-Variablen gesetzt werden.

Setzen des Log-Levels:

```shell script
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar sql2json.jar
```

Log-Level: "trace", "debug", "info", "warn", "error" oder "off". Default: "info"

Siehe [javadoc der Klasse SimpleLogger](http://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html) 
bezüglich der weiteren Konfigurationsmöglichkeiten des Loggings.

### Beispiel mit Parametern und Logging:

```shell script
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/template.json \
  -o $(pwd)/result.json
```

## Konfiguration mittels Template, Trafo-Tags und Sql-Query Dateien

### Programmablauf

Für jedes Trafo-Tag, welches der Trafo im Json-Template antrifft, werden die folgenden Schritte durchgeführt. Der Trafo:
* setzt das im Trafo-Tag referenzierte SQL-Query auf die Datenbank ab. Der Pfad zur Query-Datei wird relativ
zum Template-Pfad aufgelöst, damit die Sql-Dateien auch in Unterverzeichnissen geordnet werden können.
* verarbeitet das SQL Resultset in ein Json-Element.
* ersetzt im Output.json das Trafo-Tag mit dem Json-Element.

### Trafo-Tag

Das Trafo-Tag ist ein Json-Objekt, dessen Name mit "$trafo:" beginnt: `{"$trafo:elem": "object.sql"}`

**Typen:**
* **"$trafo:elem":** Rendert ein einzelnes Json-Element in das Output-Json. Typen von Json-Elementen:
  * "Primitive" Werte (String, Number, Boolean, Null)
  * Liste von Elementen: `[...]`
  * Objekt: `{...}`
* **"$trafo:list":** Rendert eine Liste von Json-Elementen in das Output-Json.
  * Die Elemente der Liste können wiederum Primitivwerte, Objekte oder Listen sein.
* **"$trafo:set":** Rendert ein Objekt mit Name-Wert-Paaren in das Output-Json.
  * Der Name in den Paaren ist ein String.
  * Die Werte in den Paaren können wiederum Primitivwerte, Objekte oder Listen sein.

### Beispiel für Trafo-Tag `{"$trafo:elem": "..."}`

**Template-Ausschnitt**

```json
{
	"fuu": "...",
	"layer": {"$trafo:elem": "element.sql"},
	"bar": "..."
}
```

**Query-Rückgabe**

|buz|
|---|
|ch.so.agi.gemeindegrenzen|

Der Trafo verwendet die erste zurückgegebene Spalte des Resultsets.
* Spaltenname ist egal
* Aus dem Db-Typ der Spalte leitet der Trafo das passende Json-Element ab
  * Json, Jsonb -> Objekt oder Liste
  * Varchar, Number, .... -> entsprechender Json "Primitivtyp"

**Json-Ausgabe**

```json
{
	"fuu": "...",
	"layer": "ch.so.agi.gemeindegrenzen",
	"bar": "..."
}
```

### Beispiel für Trafo-Tag `{"$trafo:list": "..."}`

**Template-Ausschnitt**

```json
{
	"fuu": "...",
	"layers": {"$trafo:list": "objectlist.sql"},
	"bar": "..."
}
```

**Query-Rückgabe**

|buz|
|---|
|{"id": "ch.so.agi.gemeindegrenzen", "title": "Gemeindegrenzen", "visible": true }|
|{"id": "ch.so.agi.bezirksgrenzen", "title": "Bezirksgrenzen", "visible": false }|

Verhalten bezüglich der Spaltentypen identisch wie bei `{"$trafo:elem": "..."}`

**Json-Ausgabe**

```json
{
	"fuu": "...",
	"layers": [{
			"id": "ch.so.agi.gemeindegrenzen",
			"title": "Gemeindegrenzen",
			"visible": true
		},
		{
			"id": "ch.so.agi.bezirksgrenzen",
			"title": "Bezirksgrenzen",
			"visible": false
		}
	],
	"bar": "..."
}
```

### Beispiel für Trafo-Tag `{"$trafo:set": "..."}`

**Template-Ausschnitt**

```json
{
	"fuu": "...",
	"layers": {"$trafo:set": "objectset.sql"},
	"bar": "..."
}
```

**Query-Rückgabe**

|fuu|bar|
|---|---|
|ch.so.agi.gemeindegrenzen|{"title": "Gemeindegrenzen", "visible": true }|
|ch.so.agi.bezirksgrenzen|{"title": "Bezirksgrenzen", "visible": false }|

Der Trafo verwendet die ersten beiden zurückgegebenen Spalten des Resultsets.
* Die Spaltennamen sind egal
* Der Db-Typ der ersten Spalte muss ein String sein (varchar, ...)
* Aus dem Db-Typ der zweiten Spalte leitet der Trafo das passende Json-Element ab
  * Json, Jsonb -> Objekt oder Liste
  * Varchar, Number, .... -> entsprechender Json "Primitivtyp"

**Json-Ausgabe**

```json
{
	"fuu": "...",
	"layers": {
		"ch.so.agi.gemeindegrenzen": {
			"title": "Gemeindegrenzen",
			"visible": true
		},
		"ch.so.agi.bezirksgrenzen": {
			"title": "Bezirksgrenzen",
			"visible": false
		}
	},
	"bar": "..."
}
```

### Korrekte Komplettkonfigurationen

Die Integrationstests sind gute erläuternde Komplettkonfigurationen (mit Template, Trafo-Tag, Sql-Datei). 
Siehe die auf _ok.sh endenden Integrationstests in Ordner \[Repo-Root\]/inttest.

### Fehlerbehandlung

Beim Auftreten eines Fehlers wird das Json-Tag mit dem Fehlertext "erweitert" und in das output-json geschrieben.
Zusätzlich wird ein umfangreicher Fehleroutput auf den Error-Stream geschrieben.

Bei einem Fehler bei der Verarbeitung eines Trafo-Tag wird die Verarbeitung der weiteren Tag's **nicht** abgebrochen.
Die auftretenden Fehler werden sequentiell ausgegeben. Bei einem Fehler gibt der gestartete Java-Prozess <> 0 
als exit value zurück.