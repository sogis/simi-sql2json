package ch.so.agi.sql2json.validation;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.tag.JsonType;
import ch.so.agi.sql2json.test.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;

public class ValidatorTest {

    @Test
    void val_OK() throws Exception {

        String[] contents = deferContentsFromMethod();
        Assertions.assertDoesNotThrow(() -> Validator._validate(contents[0], contents[1]));
    }

    @Test
    void val_MultiError_Throws() throws Exception {

        String[] contents = deferContentsFromMethod();
        Assertions.assertThrows(
                Exception.class,
                () -> Validator._validate(contents[0], contents[1])
        );
    }

    @Test
    void val_SingleError_Throws() throws Exception {

        String[] contents = deferContentsFromMethod();
        Assertions.assertThrows(
                Exception.class,
                () -> Validator._validate(contents[0], contents[1])
        );
    }

    private static String[] deferContentsFromMethod(){

        Path base = Util.deferTestResourcesPathFromCallingMethod(2);

        String contentPath = base.resolve("content.json").toString();//Path.of("src/test/resources/" + methodName + "/content.json").toString();
        String schemaPath = base.resolve("schema.json").toString();//Path.of("src/test/resources/" + methodName + "/schema.json").toString();

        return new String[]{
                new TextFile(contentPath).readContentToString(),
                new TextFile(schemaPath).readContentToString()
        };
    }
}
