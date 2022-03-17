package ch.so.agi.sql2json.generator;

import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Koordiniert das Schreiben mehrerer Outputs (Json, YAML, ...).
 *
 * Bietet den sql2json Nutzerklassen den notwendigen Teil des
 * API's der Klasse com.fasterxml.jackson.core.JsonGenerator an.
 */
public class MultiWriter implements AutoCloseable {

    private final List<JsonGenerator> writers;

    public MultiWriter(Collection<TextOutput> outputs){
        List<JsonGenerator> childWriter = createGeneratorsForOutputs(outputs);
        this.writers = childWriter;
    }

    private static List<JsonGenerator> createGeneratorsForOutputs(Collection<TextOutput> outputs){

        LinkedList<JsonGenerator> res = new LinkedList<>();

        for(TextOutput o : outputs){
            OutputStream s = null;

            try {
                s = new FileOutputStream(o.getTextFile());
            } catch (FileNotFoundException e) {
                throw new TrafoException(e);
            }

            JsonGenerator gen = createForOutputFormat(o.getFormat(), s);

            res.add(gen);
        }
        return res;
    }

    private static JsonGenerator createForOutputFormat(OutputFormat format, OutputStream stream){

        JsonGenerator res = null;

        try{
            if(format == OutputFormat.JSON) {
                res = new JsonFactory().createGenerator(stream);
            }
            else if(format == OutputFormat.YAML){
                res = new YAMLFactory().createGenerator(stream);
            }
            else {
                throw new TrafoException("Generator for requested OutputFormat {0} does not exist", format);
            }
        } catch (IOException e) {
            throw new TrafoException(e);
        }

        return res;
    }
    
    public void writeStartArray() {
        writers.forEach(exWrap(writer -> writer.writeStartArray()));
    }
    
    public void writeEndArray() {
        writers.forEach(exWrap(writer -> writer.writeEndArray()));
    }
    
    public void writeStartObject() {
        writers.forEach(exWrap(writer -> writer.writeStartObject()));
    }
    
    public void writeEndObject() {
        writers.forEach(exWrap(writer -> writer.writeEndObject()));
    }
    
    public void writeFieldName(String s) {
        writers.forEach(exWrap(writer -> writer.writeFieldName(s)));
    }
    
    public void writeString(String s) {
        writers.forEach(exWrap(writer -> writer.writeString(s)));
    }
    
    public void writeRawValue(String s) {
        writers.forEach(exWrap(writer -> writer.writeRawValue(s)));
    }
    
    public void writeNumber(int i) {
        writers.forEach(exWrap(writer -> writer.writeNumber(i)));
    }

    public void writeNumber(double d) {
        writers.forEach(exWrap(writer -> writer.writeNumber(d)));
    }

    public void writeNumber(float f) {
        writers.forEach(exWrap(writer -> writer.writeNumber(f)));
    }

    public void writeNumber(String s) {
        writers.forEach(exWrap(writer -> writer.writeNumber(s)));
    }
    
    public void writeBoolean(boolean b) {
        writers.forEach(exWrap(writer -> writer.writeBoolean(b)));
    }
    
    public void writeNull() {
        writers.forEach(exWrap(writer -> writer.writeNull()));
    }
    
    public void writeObject(Object o) throws IOException {
        writers.forEach(exWrap(writer -> writer.writeObject(o)));
    }

    public void writeStringField(String name, String value) {
        writers.forEach(exWrap(writer -> writer.writeStringField(name, value)));
    }

    @Override
    public void close() throws Exception {
        writers.forEach(exWrap(writer -> writer.close()));
    }

    /**
     * Wrapper for methods throwing a checked Exception.
     *
     * The Exception is wrapped into a (unchecked) TrafoException that is thrown.
     */
    private static <T> Consumer<T> exWrap(ExWrapper<T, Exception> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                throw new TrafoException(ex);
            }
        };
    }
}
