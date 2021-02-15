package ch.so.agi.sql2json.tag;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.exception.ExType;
import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Properties;

public class SetTag extends Tag {

    private static Logger log = LoggerFactory.getLogger(SetTag.class);
    private static final String TEMPLATE_SUFFIX = "set";

    @Override
    protected String fullTagName(){
        return TEMPLATE_PREFIX + TEMPLATE_SUFFIX;
    }

    @Override
    public void execSql(String sqlFileName, JsonGenerator gen){
        log.info("{}: Executing sql ...");

        String url = Configuration.valueForKey(Configuration.DB_CONNECTION);
        Properties props = new Properties();
        props.setProperty("user", Configuration.valueForKey(Configuration.DB_USER));
        props.setProperty("password", Configuration.valueForKey(Configuration.DB_PASSWORD));

        try(Connection conn = DriverManager.getConnection(url, props)){
            String sql = Tags.sqlFromFile(sqlFileName);

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);
            JsonType type = checkColumnStructure(rs, sqlFileName);

            gen.writeStartObject();

            int rowCount = 0;
            while (rs.next())
            {
                gen.writeFieldName(rs.getString(1));
                Tags.writeValue(rs, 2, type, gen);
                rowCount++;
            }
            rs.close();

            log.info("{}: Processed {} rows.", sqlFileName, rowCount);
            if(rowCount == 0)
                throw new TrafoException(ExType.NO_ROWS, "{0}: Query returned no rows", sqlFileName);

            gen.writeEndObject();
        }
        catch(Exception e){
            if(e instanceof TrafoException)
                throw (TrafoException)e;
            else
                throw new TrafoException(e);
        }
    }

    private static JsonType checkColumnStructure(ResultSet rs, String fileName) throws Exception{

        ResultSetMetaData meta = rs.getMetaData();

        int numCols = meta.getColumnCount();
        if (numCols < 2)
            throw new TrafoException(ExType.MISSING_COLUMNS,
                    "{0}: Query for SetTag must return two columns", fileName);
        else if (numCols > 2)
            log.warn("{}: Query returned more than two columns. Using first for key and second for value (json element).", fileName);

        JsonType keyType = Tags.inferColType(meta, 1, fileName);
        if(keyType != JsonType.STRING)
            throw new TrafoException(ExType.WRONG_KEY_COLUMNTYPE, "{0}: first column must be a string type for the json key", fileName);

        return Tags.inferColType(meta, 2, fileName);
    }
}
