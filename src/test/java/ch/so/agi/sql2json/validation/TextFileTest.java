package ch.so.agi.sql2json.validation;

import ch.so.agi.sql2json.test.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class TextFileTest {

    private static final String LAST_LINE = "$LAST_LINE$";

    @Test
    public void file_localRead_OK(){
        Path filePath = Util.deferTestResourcesPathFromCallingMethod().resolve("file.txt");

        TextFile file = new TextFile(filePath.toString());
        String fileContent = file.readContentToString();

        Assertions.assertTrue(fileContent.contains(LAST_LINE));
    }

    @Test
    public void file_remoteRead_OK(){

    }

    @Test
    public void file_localRead_missing_ERROR(){

    }

    @Test
    public void file_remoteRead_missing_ERROR(){

    }
}
