package ch.so.agi.sql2json.tag;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.TextFileReader;
import ch.so.agi.sql2json.exception.ExType;
import ch.so.agi.sql2json.exception.TrafoException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

public class Tags {

    private static Logger log = LoggerFactory.getLogger(Tags.class);

    static String sqlFromFile(String relFilePath){

        String templatePath = Configuration.valueForKey(Configuration.TEMPLATE_PATH);
        TextFileReader reader = TextFileReader.create(templatePath, relFilePath);

        return  reader.readContentToString();
    }

    static JsonType inferColType(ResultSetMetaData meta, int colIdx, String fileName) throws Exception {

        JsonType res = null;

        int colType = meta.getColumnType(colIdx);

        if(colType == 1111) // 1111 --> json.
            res = JsonType.JSON_ELEMENT;
        else if (colType == Types.VARCHAR || colType == Types.CHAR || colType == Types.NCHAR ||
                colType == Types.NVARCHAR || colType == Types.LONGNVARCHAR || colType == Types.LONGVARCHAR)
            res = JsonType.STRING;
        else if(colType == Types.INTEGER || colType == Types.DECIMAL || colType == Types.FLOAT || colType == Types.DOUBLE)
            res = JsonType.NUMBER;
        else if(colType == Types.BOOLEAN || colType == -7) //don't know why -7. Found out in unit test.
            res = JsonType.BOOLEAN;
        else
            throw new TrafoException(ExType.VAL_COLUMNTYPE_UNKNOWN.VAL_COLUMNTYPE_UNKNOWN,
                    "{0}: Query returns unsupported column type {1}.", fileName, meta.getColumnTypeName(colIdx));

        log.debug("{}: Inferred json type {} for column {}", fileName, res, meta.getColumnName(colIdx) );

        return res;
    }

    static void writeValue(ResultSet rs, int colIdx, JsonType jsonType, JsonGenerator gen) throws Exception {

        Object val = rs.getObject(colIdx);
        if(rs.wasNull()) {
            gen.writeNull();
            return;
        }

        if(jsonType == JsonType.JSON_ELEMENT){
            gen.writeRawValue(val.toString());
        }
        else if(jsonType == JsonType.STRING){
            gen.writeString(val.toString());
        }
        else if(jsonType == JsonType.NUMBER){
            gen.writeNumber(val.toString());
        }
        else if(jsonType == JsonType.BOOLEAN){
            gen.writeBoolean(rs.getBoolean(colIdx));
        }

        gen.writeRaw(System.lineSeparator());
    }
}
