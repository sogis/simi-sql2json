package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.TrafoException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Traverses the json template and passes the json elements to the router for processing.
 */
public class TemplateWalker {

    private static Logger log = LogManager.getLogger(TemplateWalker.class);

    public static void walkTemplate(String template, OutputStream output) throws Exception{

        JsonFactory factory = new JsonFactory();

        try(JsonParser parser = factory.createParser(template); JsonGenerator gen = factory.createGenerator(output)){

            JsonElementRouter router = new JsonElementRouter(gen);

            while(parser.nextToken() != null){
                JsonToken tok = parser.currentToken();

                if(JsonToken.START_OBJECT.equals(tok)){
                    log.debug("START_OBJECT");

                    router.objStartElem();
                }
                else if(JsonToken.END_OBJECT.equals(tok)){
                    log.debug("END_OBJECT");

                    router.objEndElem();
                }
                else if(JsonToken.FIELD_NAME.equals(tok)){
                    log.debug("FIELD_NAME");

                    router.paraName(parser.getCurrentName());
                }
                else if(JsonToken.VALUE_STRING.equals(tok)) {
                    log.debug("VALUE_STRING");

                    router.value(tok, parser.getText());
                }
                else if(JsonToken.VALUE_NULL.equals(tok)) {
                    log.debug("VALUE_NULL");

                    router.value(tok, null);
                }
                else if(JsonToken.VALUE_FALSE.equals(tok)) {
                    log.debug("VALUE_FALSE");

                    router.value(tok, false);
                }
                else if(JsonToken.VALUE_TRUE.equals(tok)) {
                    log.debug("VALUE_FALSE");

                    router.value(tok, true);
                }
                else if(JsonToken.VALUE_NUMBER_FLOAT.equals(tok)) {
                    log.debug("VALUE_NUMBER_FLOAT");

                    router.value(tok, parser.getFloatValue());
                }
                else if(JsonToken.VALUE_NUMBER_INT.equals(tok)) {
                    log.debug("VALUE_NUMBER_INT");

                    router.value(tok, parser.getIntValue());
                }
                else if(JsonToken.START_ARRAY.equals(tok)) {
                    log.debug("START_ARRAY");

                    router.arrayStartElem();
                }
                else if(JsonToken.END_ARRAY.equals(tok)) {
                    log.debug("END_ARRAY");

                    router.arrayEndElem();
                }
                else {
                    throw new TrafoException("Processing the token {0} is not implemented", tok);
                }
            }
        }
    }
}
