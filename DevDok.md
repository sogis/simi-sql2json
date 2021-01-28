# Entwicklerdokumentation

## Funktionsweise des sql2json Trafo

Der sql2json Transformator (Trafo) traversiert das Json-Template und "besucht" dabei jedes im Json enthaltene Json-Element.

Die Klasse ObjectElementBuffer erkennt anhand der traversierten Json-Elemente, ob es sich bei einer Elemente-Sequenz 
um ein Json-Tag handeln kann oder nicht. Sobald die Sequenz nicht mehr auf ein Json-Tag passt, spühlt der
ObjectElementBuffer die zurückbehaltenen Json-Elemente mittels flush(...) in den Output und initialisiert sich neu.

Falls die Sequenz beim traversieren des Objekt-Endelements "`}`" immernoch auf ein Json-Tag passt, wird mit der Methode
Tag.forName(...) ermittelt, ob ein passendes Tag vorhanden ist. Falls vorhanden, führt das Tag mittels Methode 
execSql(...) das entsprechende SQL-Query aus. Das in Json-Element(e) umgewandelte ResultSet wird in den Output geschrieben.

### Vom ObjectElementBuffer erkannte Json-Element-Sequenz

|Json-Element|Beschreibung|
|---|---|
|`{`|Start von Json-Objekt|
|`"fuu":`|Name des ersten Name-Wert-Paares|
|`"bar"`|String-Wert des ersten Name-Wert-Paares. Falls kein String passt die Sequenz nicht mehr.|
|`}`|Ende von Json-Objekt. Vor dem Ende darf nur genau ein Name-Wert-Paar auftreten, sonst passt die Sequenz nicht mehr.|

## Code Erweitern mit weiterem Json-Tag

Dafür notwendige Arbeiten:
* Neue Tag-Klasse erstellen, welche vererbt von ch.so.agi.sql2json.tag.Tag
* Die Methoden fullTagName(...) und execSql(...) überschreiben und implementieren.
* Unittest's schreiben
* Falls zutreffend: Integrationstests schreiben

## Entwicklungsabhängigkeiten

Die Unit-Tests setzen Queries gegen eine docker-basierte Entwicklungs-DB ab (Auf mocking wurde verzichtet). 
Entsprechend muss zum Ausführen der Unit-Tests die Entwicklungs-DB gestartet sein. Starten mittels Shell
[run_for_development.sh](testdb/run_for_development.sh).