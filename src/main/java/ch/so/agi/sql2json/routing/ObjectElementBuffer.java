package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.StringReader;

/**
 * Buffers Elements for the JsonElementRouter and has the knowledge
 * whether the json object elments match the "signature" of the
 * trafo-tags
 */
public class ObjectElementBuffer {

    private static Logger log = LoggerFactory.getLogger(ObjectElementBuffer.class);

    private JsonGenerator gen;

    private boolean objStarted;
    private String name;

    private String value;
    private  int stringValueCallCount;

    public ObjectElementBuffer(JsonGenerator gen){
        this.gen = gen;

        reset();
    }

    private State state(){
        State state = null;

        if(objStarted){
            if(name != null){
                if(stringValueCallCount == 0)
                    state = State.CAND_NAMED;
                else if(stringValueCallCount == 1)
                    state = State.CAND_COMPLETE;
                else { //multible values
                    state = State.NO_CAND;
                }
            }
            else {
                state = State.CAND_STARTED;
            }
        }
        else { //not started
            state = State.NO_CAND;
        }

        return state;
    }

    public boolean isComplete(){
        return state() == State.CAND_COMPLETE;
    }

    public void objStartElem(){

        State s = state();
        if(s != State.NO_CAND){
            flush(s);
        }
        else {
            reset();
        }

        this.objStarted = true;

        log.debug("objStartElem(). endstate: {}", state());
    }

    public void paraName(String name) {

        State s = state();
        if(s == State.CAND_STARTED){
            this.name = name;
        }
        else {
            flush(s);

            try {
                gen.writeFieldName(name);
            }
            catch (Exception e){
                throw new TrafoException(e);
            }
        }

        log.debug("paraName(). name: {}, endstate: {}", name, state());
    }

    private void flush(State currState){
        //log.debug("flushing... state: {}", currState);
        try {
            if (currState == State.CAND_STARTED) {
                gen.writeStartObject();
            }
            else if(currState == State.CAND_NAMED){
                gen.writeStartObject();
                gen.writeFieldName(this.name);
            }
            else if(currState == State.CAND_COMPLETE){
                gen.writeStartObject();
                gen.writeFieldName(this.name);
                gen.writeString(this.value);
            }

            reset();
        }
        catch(Exception e){
            throw new TrafoException(e);
        }
    }

    public void flush(){
        State s = state();
        flush(s);
    }

    public void flushWithException(Exception e){
        flush();

        try {
            gen.writeStringField("exception", e.toString());
        }
        catch(Exception ex){
            throw new TrafoException(ex);
        }
    }

    public void value(JsonToken type, Object value) {

        boolean candValue = false;
        if(type == JsonToken.VALUE_STRING){

            if(state() == State.CAND_NAMED){
                this.value = (String)value;
                candValue = true;
            }

            stringValueCallCount++;
        }

        if(!candValue) {
            flush(state());
            writePrimitiveValue(type, value);
        }

        log.debug("value(). type: {}, value: {}, endstate: {}", type, value, state());
    }

    public void reset(){
        this.objStarted = false;
        this.name = null;
        this.value = null;
        this.stringValueCallCount = 0;
    }

    private void writePrimitiveValue(JsonToken type, Object value) {
        log.debug("Writing primitive value '{}' ...", value);
        try {
            if(type == JsonToken.VALUE_STRING)
                gen.writeString((String)value);
            else if(type == JsonToken.VALUE_NUMBER_INT)
                gen.writeNumber((Integer)value);
            else if(type == JsonToken.VALUE_NUMBER_FLOAT) {
                if(value instanceof Double)
                    gen.writeNumber((Double)value);
                else
                    gen.writeNumber((Float)value);
            }
            else if(type == JsonToken.VALUE_TRUE || type == JsonToken.VALUE_FALSE)
                gen.writeBoolean( (Boolean)value );
            else if(type == JsonToken.VALUE_NULL) {
                gen.writeNull();
            }
            else
                throw new TrafoException("Writing json value for type {0} is not implemented", type);
        }
        catch(Exception e){
            if(e instanceof TrafoException)
                throw (TrafoException)e;
            else
                throw new TrafoException(e);
        }
    }

    private enum State {
        NO_CAND, CAND_STARTED, CAND_NAMED, CAND_COMPLETE
    }

    public String getName(){ return name; }
    public String getValue(){ return value; }
}
