package ch.so.agi.sql2json.template;

import ch.so.agi.sql2json.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;



public class Template {

    private static final String TEMPLATE_PREFIX = "$trafo:";

    public static Template forName(String name){

        if(name.startsWith(TEMPLATE_PREFIX))
            return new Template();
        else
            return null;
    }

    public void execSql(String sqlFileName, JsonGenerator gen){
        try {
            gen.writeString("Hello World");
        }
        catch (Exception e){
            throw new TrafoException(e);
        }
    }
}
