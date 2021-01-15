package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private  int valueCallCount;

    public ObjectElementBuffer(JsonGenerator gen){
        this.gen = gen;

        reset();
    }

    private State state(){
        State state = null;

        if(objStarted){
            if(name != null){
                if(valueCallCount == 0)
                    state = State.CAND_NAMED;
                else if(valueCallCount == 1)
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
/*
    private State state(){
        State state = null;

        if(name == null || valueCallCount > 1){
            if(!objStarted)
                state = State.NO_CAND;
            else
                state = State.CAND_STARTED;
        }
        else {
            if(name != null){

                if(valueCallCount == 0){
                    state = State.CAND_NAMED;
                }
                else if(valueCallCount == 1){
                    state = State.CAND_COMPLETE;
                }
                else { //valueCallCount > 1
                    state = State.NO_CAND;
                }
            }
        }

        return state;
    }

 */

    public boolean isComplete(){
        return state() == State.CAND_COMPLETE;
    }

    public void objStartElem(){

        State s = state();
        if(s == State.CAND_NAMED){
            try {
                gen.writeStartObject();
                gen.writeFieldName(this.name);
            }
            catch(Exception e){
                throw new TrafoException(e);
            }
        }

        reset();
        this.objStarted = true;

        log.info("objStartElem(). endstate: {}", state());
    }

    public void paraName(String name) {

        State s = state();
        if(s == State.CAND_STARTED){
            this.name = name;
        }
        else {
            try {
                if (s == State.CAND_COMPLETE) { // write first param
                    gen.writeStartObject();
                    gen.writeFieldName(this.name);
                    gen.writeString(this.value);
                }

                gen.writeFieldName(name);
            }
            catch(Exception e){
                throw new TrafoException(e);
            }
        }

        log.info("paraName(). name: {}, endstate: {}", name, state());
    }

    public void value(JsonToken type, Object value) {

        State s = state();
        if(s == State.CAND_NAMED && value != null && value instanceof String){
            this.value = (String)value;
        }
        else{
            writePrimitiveValue(type, value);
        }

        valueCallCount++;
        log.info("value(). type: {}, value: {}, endstate: {}", type, value, state());
    }

    public void reset(){
        this.objStarted = false;
        this.name = null;
        this.value = null;
        this.valueCallCount = 0;
    }

    private void writePrimitiveValue(JsonToken type, Object value) {
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
            else if(type == JsonToken.VALUE_NULL)
                gen.writeNull();
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
