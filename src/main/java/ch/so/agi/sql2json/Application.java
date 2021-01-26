package ch.so.agi.sql2json;

import ch.so.agi.sql2json.exception.TrafoException;
import ch.so.agi.sql2json.log.Logging;
import ch.so.agi.sql2json.routing.TemplateWalker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class Application {

    private static Logger log = LogManager.getLogger(Application.class);

    //private static Configuration conf = null;

    public static void main(String[] args){

        try {
            Configuration.createConfig4Args(args);

            if(Configuration.helpPrinted())
                return;

            String level = Configuration.valueForKey(Configuration.LOG_LEVEL);
            Logging.initToLogLevel(level);

            Configuration.assertComplete();

            String template = loadTemplate(Configuration.valueForKey(Configuration.TEMPLATE_PATH));

            String outPutPath = Configuration.valueForKey(Configuration.OUTPUT_PATH);
            OutputStream output = new FileOutputStream(outPutPath);

            try {
                TemplateWalker.walkTemplate(template, output);
                log.info("Output json written to {}", outPutPath);
            }
            finally {
                output.close();
            }
        }
        catch (Exception e){
            log.error("Exception occured. Exiting...", e);
        }
    }

    public static String loadTemplate(String path){

        String json = null;
        try (FileInputStream fis = new FileInputStream(path)) {
            byte[] data = fis.readAllBytes();
            json = new String(data);
        }
        catch(Exception e){
            throw new TrafoException(e);
        }
        return json;
    }


    //public static Configuration conf(){return conf;}
}
