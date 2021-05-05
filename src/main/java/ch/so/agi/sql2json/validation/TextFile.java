package ch.so.agi.sql2json.validation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class TextFile {

    private String path;

    public TextFile(String path){
        this.path = path;
    }

    public String readContentToString(){

        String res = null;

        InputStream is = createFromPath(path);
        try(InputStream ins = is){
            byte[] bytes = ins.readAllBytes();
            res = new String(bytes, StandardCharsets.UTF_8);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        return res;
    }

    private static InputStream createFromPath(String filePath){
        InputStream is = null;

        try {
            if (filePath.startsWith("http")) {
                URL url = new URL(filePath);
                is = url.openStream();
            }
            else {
                is = new FileInputStream(filePath);
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        return is;
    }
}
