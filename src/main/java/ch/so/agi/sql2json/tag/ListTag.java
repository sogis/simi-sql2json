package ch.so.agi.sql2json.tag;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Properties;

public class ListTag extends BaseTag {

    private static Logger log = LogManager.getLogger(ListTag.class);
    private static final String TEMPLATE_SUFFIX = "list";

    private static final int VAL_JSON_ELEMENT = 1;
    private static final int VAL_STRING = 2;
    private static final int VAL_NUMBER = 3;
    private static final int VAL_BOOLEAN = 4;

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
            int jsonType = inferJsonFromColumnType(rs, sqlFileName);

            gen.writeStartArray();

            int rowCount = 0;
            while (rs.next())
            {
                writeElement(rs, jsonType, gen);
                rowCount++;
            }
            rs.close();

            log.debug("Processed {} rows for query {}", rowCount, sqlFileName);
            if(rowCount == 0)
                throw new TrafoException(TrafoException.Type.NO_ROWS, "Query {0} returned no rows", sqlFileName);

            gen.writeEndArray();
        }
        catch(Exception e){
            if(e instanceof TrafoException)
                throw (TrafoException)e;
            else
                throw new TrafoException(e);
        }
    }

    private static int inferJsonFromColumnType(ResultSet rs, String fileName) throws Exception{

        int res = -1;

        ResultSetMetaData meta = rs.getMetaData();

        int numCols = meta.getColumnCount();
        if(numCols > 1)
            log.warn("Query from {} returned more than one column. Using first column.", fileName);

        log.info("Column type: int '{}' name '{}'", meta.getColumnType(1), meta.getColumnTypeName (1));

        int colType = meta.getColumnType(1);

        if(colType == 1111) // 1111 --> json.
            res = VAL_JSON_ELEMENT;
        else if (colType == Types.VARCHAR || colType == Types.CHAR || colType == Types.NCHAR ||
                colType == Types.NVARCHAR || colType == Types.LONGNVARCHAR || colType == Types.LONGVARCHAR)
            res = VAL_STRING;
        else if(colType == Types.INTEGER || colType == Types.DECIMAL || colType == Types.FLOAT || colType == Types.DOUBLE)
            res = VAL_NUMBER;
        else if(colType == Types.BOOLEAN)
            res = VAL_BOOLEAN;
        else
            throw new TrafoException(TrafoException.Type.COLUMNTYPE_UNKNOWN, "query from {0} returns unsupported column type {1}.", fileName, meta.getColumnTypeName(1));

        return res;
    }

    private static void writeElement(ResultSet rs, int jsonType, JsonGenerator gen) throws Exception {

        String val = rs.getObject(1).toString();
        if(rs.wasNull()) {
            gen.writeNull();
            return;
        }

        if(jsonType == VAL_JSON_ELEMENT){
            gen.writeObject(val);
        }
        else if(jsonType == VAL_STRING){
            gen.writeString(val);
        }
        else if(jsonType == VAL_NUMBER){
            gen.writeNumber(val);
        }
        else if(jsonType == VAL_BOOLEAN){
            gen.writeBoolean(rs.getBoolean(1));
        }
    }
}
