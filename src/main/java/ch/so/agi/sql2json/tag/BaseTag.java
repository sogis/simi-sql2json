package ch.so.agi.sql2json.tag;

import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;

public class BaseTag {

    public static final String MARKER_VALUE = "$BaseTag$";

    private static Logger log = LogManager.getLogger(BaseTag.class);

    protected static final String TEMPLATE_PREFIX = "$trafo:";
    private static final String TEMPLATE_SUFFIX = "base";

    private static HashMap<String, BaseTag> templates = null;

    static {
        templates = new HashMap<>();

        BaseTag base = new BaseTag();
        templates.put(base.fullTagName(), base);

        ListTag arr = new ListTag();
        templates.put(arr.fullTagName(), arr);
    }

    public static BaseTag forName(String name){
        BaseTag t = templates.getOrDefault(name, null);

        if(t == null && name.startsWith(TEMPLATE_PREFIX))
            log.warn("Property '{}' seems to point to template tag, but no matching tag was found.");

        return t;
    }

    protected String fullTagName(){
        return TEMPLATE_PREFIX + TEMPLATE_SUFFIX;
    }

    public void execSql(String sqlFileName, JsonGenerator gen){
        log.info("execSql()");

        try {
            String output = MessageFormat.format("'{' \"{0}\" : \"{1}\" '}'", fullTagName(), MARKER_VALUE);
            gen.writeObject(output);
        }
        catch (Exception e){
            throw new TrafoException(e);
        }
    }
}
