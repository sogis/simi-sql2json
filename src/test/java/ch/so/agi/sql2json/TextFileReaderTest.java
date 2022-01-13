package ch.so.agi.sql2json;

        import ch.so.agi.sql2json.exception.TrafoException;
        import ch.so.agi.sql2json.test.Util;
        import org.junit.jupiter.api.Assertions;
        import org.junit.jupiter.api.Test;

        import java.net.URI;
        import java.nio.file.Path;

public class TextFileReaderTest {

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
        Path base = Util.deferTestResourcesAbsPathFromCallingMethod(null);
        Path filePath = base.resolve("file.txt");

        TextFileReader file = TextFileReader.create(filePath.toString());
        String fileContent = file.readContentToString();

        Assertions.assertTrue(fileContent.contains(LAST_LINE));
    }

    @Test
    public void file_remoteRead_OK(){
        String path = RESSOURCES_IN_REPO.resolve("file.txt").toString();

        TextFileReader file = TextFileReader.create(path);
        String fileContent = file.readContentToString();

        Assertions.assertTrue(fileContent.contains(LAST_LINE));
    }

    @Test
    public void file_localRead_missing_ERROR(){

        Path filePath = Util.deferTestResourcesAbsPathFromCallingMethod(null).resolve("nonExistingDummy.txt");
        TextFileReader file = TextFileReader.create(filePath.toUri());

        Assertions.assertThrows(Exception.class, () -> file.readContentToString());
    }

    @Test
    public void file_remoteRead_missing_ERROR(){

        String path = RESSOURCES_IN_REPO.resolve("nonExistingDummy.txt").toString();
        TextFileReader file = TextFileReader.create(path);

        Assertions.assertThrows(Exception.class, () -> file.readContentToString());
    }

    @Test
    public void file_remoteSiblingRead_OK(){
        URI mainFile = RESSOURCES_IN_REPO.resolve("nonExistingDummy.txt");

        TextFileReader siblingFile = TextFileReader.create(mainFile,  "file.txt");
        String fileContent = siblingFile.readContentToString();

        Assertions.assertTrue(fileContent.contains(LAST_LINE));
    }

    @Test
    public void file_relativePathThrowsTrafoEx_OK(){
        Assertions.assertThrows(TrafoException.class, () -> TextFileReader.create("./nonExistingRelativeFile.txt"));
    }
}

