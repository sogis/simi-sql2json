package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.TrafoException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Traverses the json template and passes the json elements to the router for processing.
 */
public class TemplateWalker {

    private static Logger log = LoggerFactory.getLogger(TemplateWalker.class);

    public static void walkTemplate(InputStream template, OutputStream output) throws Exception{

        JsonFactory factory = new JsonFactory();

        try(JsonParser parser = factory.createParser(template); JsonGenerator gen = factory.createGenerator(output)){

            JsonElementRouter router = new JsonElementRouter(gen);

            while(parser.nextToken() != null){
                JsonToken tok = parser.currentToken();

                if(JsonToken.START_OBJECT.equals(tok)){
                    log.info("START_OBJECT");

                    router.objStartElem();
                }
                else if(JsonToken.END_OBJECT.equals(tok)){
                    log.info("END_OBJECT");

                    router.objEndElem();
                }
                else if(JsonToken.FIELD_NAME.equals(tok)){
                    log.info("FIELD_NAME");

                    router.paraName(parser.getCurrentName());
                }
                else if(JsonToken.VALUE_STRING.equals(tok)) {
                    log.info("VALUE_STRING");

                    router.value(tok, parser.getText());
                }
                else {
                    throw new TrafoException("Processing the token {0} is not implemented", tok);
                }
            }
        }
    }
}
