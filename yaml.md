# Erweiterung für den Output von YAML

## Eigenschaften der Lösung

* Nach dem Schreiben des *.json im Zielverzeichnis wird daneben ein *.yaml angelegt
* Umwandlung mittels Jackson
* Wrapping der 12 notwendigen Generator-Methoden in die Basisklasse "TextFileGenerator extends com.fasterxml.jackson.core.base.GeneratorBase", welche Situationsabhängig ein Json und/oder ein YAML schreibt.
  * Nutzen der Functional Interfaces und Aufruf dieser. Etwa:   
    ```
    public void writeString(String val){
      //Function<Integer, String> intToString = Object::toString;
      
      //Darum für gen.writeString("fuu") siehe https://www.baeldung.com/java-8-functional-interfaces
      BiConsumer<Generator, String> f = Generator::writeString;
      f.accept(myGenerator, "fuu");

      //Mittels Streaming der Collection der Generator
      // Lambda mit zwei Parametern (p1, p2) -> System.out.println("Multiple parameters: " + p1 + ", " + p2);

      childrenStream.forEach(childGen -> childGen.writeString(val))
    }
    ```