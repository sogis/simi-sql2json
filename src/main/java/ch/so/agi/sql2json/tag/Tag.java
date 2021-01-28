package ch.so.agi.sql2json.tag;

import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;

public class Tag {

    public static final String MARKER_VALUE = "$BaseTag$";

    private static Logger log = LoggerFactory.getLogger(Tag.class);

    protected static final String TEMPLATE_PREFIX = "$trafo:";
    private static final String TEMPLATE_SUFFIX = "base";

    private static HashMap<String, Tag> templates = null;

    static {
        templates = new HashMap<>();

        Tag base = new Tag();
        templates.put(base.fullTagName(), base);

        ListTag arr = new ListTag();
        templates.put(arr.fullTagName(), arr);

        SetTag set = new SetTag();
        templates.put(set.fullTagName(), set);

        ElementTag elem = new ElementTag();
        templates.put(elem.fullTagName(), elem);
    }

    public static Tag forName(String name){
        Tag t = templates.getOrDefault(name, null);

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
