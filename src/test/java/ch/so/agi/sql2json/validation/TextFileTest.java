package ch.so.agi.sql2json.validation;

import ch.so.agi.sql2json.test.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;

public class TextFileTest {

    private static final String LAST_LINE = "$LAST_LINE$";
    private static final String _RESSOURCES_IN_REPO = "https://raw.githubusercontent.com/simi-so/sql2json/main/src/test/resources/file_remoteRead_OK/";

    private static URI RESSOURCES_IN_REPO = null;

    static {
        try{
            RESSOURCES_IN_REPO = new URI(_RESSOURCES_IN_REPO);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void file_localRead_OK(){
        Path base = Util.deferTestResourcesPathFromCallingMethod(null);
        Path filePath = base.resolve("file.txt");

        TextFile file = new TextFile(filePath.toString());
        String fileContent = file.readContentToString();

        Assertions.assertTrue(fileContent.contains(LAST_LINE));
    }

    @Test
    public void file_remoteRead_OK(){
        String path = RESSOURCES_IN_REPO.resolve("file.txt").toString();

        TextFile file = new TextFile(path);
        String fileContent = file.readContentToString();

        Assertions.assertTrue(fileContent.contains(LAST_LINE));
    }

    @Test
    public void file_localRead_missing_ERROR(){

        Path filePath = Util.deferTestResourcesPathFromCallingMethod(null).resolve("nonExistingDummy.txt");
        TextFile file = new TextFile(filePath.toString());

        Assertions.assertThrows(Exception.class, () -> file.readContentToString());
    }

    @Test
    public void file_remoteRead_missing_ERROR(){

        String path = RESSOURCES_IN_REPO.resolve("nonExistingDummy.txt").toString();
        TextFile file = new TextFile(path);

        Assertions.assertThrows(Exception.class, () -> file.readContentToString());
    }
}
