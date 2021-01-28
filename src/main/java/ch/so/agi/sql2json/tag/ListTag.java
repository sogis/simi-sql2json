package ch.so.agi.sql2json.tag;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.exception.ExType;
import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Properties;

public class ListTag extends Tag {

    private static Logger log = LoggerFactory.getLogger(ListTag.class);
    private static final String TEMPLATE_SUFFIX = "list";

    @Override
    protected String fullTagName(){
        return TEMPLATE_PREFIX + TEMPLATE_SUFFIX;
    }

    @Override
    public void execSql(String sqlFileName, JsonGenerator gen){
        log.info("execSql()");

        String url = Configuration.valueForKey(Configuration.DB_CONNECTION);
        Properties props = new Properties();
        props.setProperty("user", Configuration.valueForKey(Configuration.DB_USER));
        props.setProperty("password", Configuration.valueForKey(Configuration.DB_PASSWORD));

        try(Connection conn = DriverManager.getConnection(url, props)){
            String sql = Tags.sqlFromFile(sqlFileName);

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);
            JsonType type = checkColumnStructure(rs, sqlFileName);

            gen.writeStartArray();

            int rowCount = 0;
            while (rs.next())
            {
                Tags.writeElement(rs,1, type, gen);
                rowCount++;
            }
            rs.close();

            log.info("{}: Processed {} rows.", sqlFileName, rowCount);
            if(rowCount == 0)
                throw new TrafoException(ExType.NO_ROWS, "Query {0} returned no rows", sqlFileName);

            gen.writeEndArray();
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
        if(numCols > 1)
            log.warn("{}: Query returned more than one column. Using first column.", fileName);

        log.info("Column type: int '{}' name '{}'", meta.getColumnType(1), meta.getColumnTypeName (1));

        return Tags.inferColType(meta, 1, fileName);
    }
}
