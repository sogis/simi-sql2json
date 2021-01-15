# sql2json

Der sql2json Transformator arbeitet pro Programmaufruf ein Json-Template mit n im Template enthaltenen Trafo-Tags ab. 
Für jedes Trafo-Tag setzt der Trafo ein Sql-Statement auf die Metadatenbank ab und ersetzt das Trafo-Tag mit dem Ergebnis des SQL-Queries.

## scratch

```json
{"tableInfo":{"schemaName":"tiger","description":"empty","pkField":"cntyidfp","tvName":"county"}}
```

```json
{"tableInfo":{"schemaName":"tiger","description":"empty","layers":{"$trafo:fuu": "bar"},"tvName":"county"}}
```