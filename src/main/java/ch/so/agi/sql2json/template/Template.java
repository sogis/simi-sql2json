package ch.so.agi.sql2json.template;

import ch.so.agi.sql2json.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Template {

    private static Logger log = LogManager.getLogger(Template.class);

    private static final String TEMPLATE_PREFIX = "$trafo:";

    public static Template forName(String name){

        if(name.startsWith(TEMPLATE_PREFIX))
            return new Template();
        else
            return null;
    }

    public void execSql(String sqlFileName, JsonGenerator gen){
        log.info("Executing sql file {}", sqlFileName);

        try {
            gen.writeString("Hello World");
        }
        catch (Exception e){
            throw new TrafoException(e);
        }
    }
}
