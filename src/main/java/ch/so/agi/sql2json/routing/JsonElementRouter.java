package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.TrafoException;
import ch.so.agi.sql2json.template.Template;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes the JsonElements to the writer or to the corresponding sql-tag
 * for appending the json from the sql resultset.
 */
public class JsonElementRouter {

    private static Logger log = LoggerFactory.getLogger(JsonElementRouter.class);

    private JsonGenerator gen;
    private ObjectElementBuffer buf;

    public JsonElementRouter(JsonGenerator gen){
        this.gen = gen;
        this.buf = new ObjectElementBuffer(gen);
    }

    public void objStartElem(){
        log.info("objStartElem()");

        buf.objStartElem();
    }

    public void objEndElem(){
        log.info("objEndElem()");

        boolean write = false;
        if (buf.isComplete()) {
            Template t = Template.forName(buf.getName());
            if (t != null)
                t.execSql(buf.getValue(), gen);
            else {
                write = true;
            }
        }
        else {
            write = true;
        }

        if(write){
            try {
                gen.writeEndObject();
            }
            catch (Exception e) {
                throw new TrafoException(e);
            }
        }

        buf.reset();
    }

    public void paraName(String name) {
        log.info("objEndElem(). name: {}", name);

        buf.paraName(name);
    }

    public void value(JsonToken type, Object value){
        log.info("value(). type: {}, value: {}", type, value);

        buf.value(type, value);
    }




}
