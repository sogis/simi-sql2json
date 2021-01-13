package ch.so.agi.sql2json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            conf.assertComplete();

//"debug", "info", "warn", "error" or "off"

        }
        catch (Exception e){
            System.err.println(e.getMessage());

            log.error("Exception occured. Exiting...", e);
        }
    }



    public static Configuration conf(){return conf;}
}
