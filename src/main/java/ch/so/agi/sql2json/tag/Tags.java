package ch.so.agi.sql2json.tag;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.exception.TrafoException;

import java.io.FileInputStream;
import java.nio.file.Path;

public class Tags {

    static String sqlFromFile(String relFilePath){
        String fileContent = null;

        Path template = Path.of(Configuration.valueForKey(Configuration.TEMPLATE_PATH));
        Path sql = template.getParent().resolve(relFilePath);

        try (FileInputStream fis = new FileInputStream(sql.toFile())) {
            byte[] data = fis.readAllBytes();
            fileContent = new String(data);
        }
        catch(Exception e){
            throw new TrafoException(e);
        }
        return fileContent;
    }


    //static Properties


}
