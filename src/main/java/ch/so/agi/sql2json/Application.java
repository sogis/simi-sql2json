package ch.so.agi.sql2json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args){
        Configuration conf = Configuration.createConfig4Args(args);

        log.info("Hello World");
    }
}
