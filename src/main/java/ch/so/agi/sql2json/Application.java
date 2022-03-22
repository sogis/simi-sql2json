package ch.so.agi.sql2json;

import ch.so.agi.sql2json.exception.TrafoException;
import ch.so.agi.sql2json.routing.TemplateWalker;
import ch.so.agi.sql2json.validation.Validator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;

public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        try {
            Configuration.createConfig4Args(args);

            if(Configuration.helpPrinted())
                return;

            Configuration.assertComplete();

            TextFileReader templateReader = TextFileReader.create(Configuration.valueForKey(Configuration.TEMPLATE_PATH));
            String template = templateReader.readContentToString();

            String outPutPath = Configuration.valueForKey(Configuration.OUTPUT_PATH);

            TemplateWalker.walkTemplate(template, new File(outPutPath));
            log.info("Output json written to {}", outPutPath);

            String schemaPath = Configuration.valueForKey(Configuration.JSON_SCHEMA);
            if(schemaPath != null && schemaPath.length() > 0){
                log.info("Validating against schema {}", schemaPath);
                Validator.validate(TextFileReader.create(outPutPath), TextFileReader.create(schemaPath));
            }
        }
        catch (Exception e){
            log.error("Exception occured. Exiting...\n");
            log.error(e.toString());
            e.printStackTrace();

            throw e;
        }
    }

    private static String replaceSuffix(String candidate, String newSuffix){
        String[] parts = candidate.split("\\.");

        if(parts.length < 2)
            throw new TrafoException("Path to data file is malformed. Is {0} but must end with file suffix. Valid examples: '/fuu/data.json', '/bar/data.yaml'", candidate);

        parts[parts.length-1] = newSuffix;

        return String.join(".", parts);
    }
}
