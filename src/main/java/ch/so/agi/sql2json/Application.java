package ch.so.agi.sql2json;

import ch.so.agi.sql2json.routing.TemplateWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;



public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    private static Configuration conf = null;

    public static void main(String[] args){

        try {
            conf = Configuration.createConfig4Args(args);

            String level = conf.getConfigValue(Configuration.LOG_LEVEL);
            if(level != null && level.length() > 0)
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", level);

            if(conf.helpPrinted())
                return;

            //conf.assertComplete();

            String json = "{\"tableInfo\":{\"schemaName\":\"tiger\",\"description\":\"empty\",\"layers\":{\"$trafo:fuu\": \"bar\"},\"tvName\":\"county\"}}";
            InputStream template = new ByteArrayInputStream(json.getBytes());
            OutputStream output = new ByteArrayOutputStream();

            try {
                TemplateWalker.walkTemplate(template, output);
            }
            finally {
                output.close();
                template.close();
            }

            log.info(output.toString());
        }
        catch (Exception e){
            System.err.println(e.getMessage());

            log.error("Exception occured. Exiting...", e);
        }
    }

    public static Configuration conf(){return conf;}
}
