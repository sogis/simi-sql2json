package ch.so.agi.sql2json.validation;

import ch.so.agi.sql2json.routing.JsonElementRouter;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Validator {

    private static Logger log = LoggerFactory.getLogger(Validator.class);

    public static boolean validate(String contentFullPath, String schemaFullPath){

        boolean res = false;

        JSONObject contentJson = readJson(contentFullPath);
        JSONObject schemaJson = readJson(schemaFullPath);

        SchemaLoader loader = SchemaLoader.builder()
                .schemaJson(schemaJson)
                .draftV7Support()
                .build();
        Schema schema = loader.load().build();

        try {
            schema.validate(contentJson);
            log.info("Validation against json schema {} passed.", schemaFullPath);
            res = true;
        } catch (ValidationException e) {
            log.info("Validation against json schema {} failed. See following detail messages.", schemaFullPath);
            log.error(e.getErrorMessage());

            List<String> innerMessages = e.getAllMessages();
            if(innerMessages != null){
                for (String message : innerMessages){
                    log.error(message);
                }
            }

            res = false;
        }

        return res;
    }

    private static JSONObject readJson(String jsonFullPath){
        JSONObject json = null;

        try(FileInputStream fis = new FileInputStream(jsonFullPath)){
            byte[] content = fis.readAllBytes();
            json = new JSONObject(new String(content, StandardCharsets.UTF_8));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

        return json;
    }
}
