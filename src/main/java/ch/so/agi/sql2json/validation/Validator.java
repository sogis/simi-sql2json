package ch.so.agi.sql2json.validation;

import ch.so.agi.sql2json.TextFileReader;
import ch.so.agi.sql2json.exception.TrafoException;
import ch.so.agi.sql2json.routing.JsonElementRouter;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Validator {

    private static Logger log = LoggerFactory.getLogger(Validator.class);

    public static void validate(TextFileReader contentFile, TextFileReader schemaFile){
        _validate(
                contentFile.readContentToString(),
                schemaFile.readContentToString()
        );
    }

    static void _validate(String content, String schema){

        try {
            JSONObject contentJson = new JSONObject(content);
            JSONObject schemaJson = new JSONObject(schema);

            SchemaLoader loader = SchemaLoader.builder()
                    .schemaJson(schemaJson)
                    .draftV7Support()
                    .build();

            Schema schemaObj = loader.load().build();
            schemaObj.validate(contentJson);
            log.info("Validation against json schema passed.");
        }
        catch (ValidationException e) {

            int innerCount = 0;
            if(e.getCausingExceptions() != null)
                innerCount = e.getCausingExceptions().size();

            if(innerCount == 0) {
                log.error("Validation against json schema failed. See following detail message.", innerCount);
                log.error(e.getMessage());
            }
            else{
                log.error("Validation against json schema failed with {} errors. See following detail messages.", innerCount);
                for (String message : e.getAllMessages()){
                    log.error(message);
                }
            }

            throw e;
        }
        catch (JSONException je){
            throw new TrafoException(je);
        }
    }
}
