package ch.so.agi.sql2json.validation;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.tag.JsonType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;

public class ValidatorTest {

    @Test
    void val_localSchema_OK() throws Exception {

        String[] paths = deferPathsFromMethod();
        boolean res = Validator.validate(paths[0], paths[1]);
        Assertions.assertTrue(res);
    }

    @Test
    void val_localSchema_SingleFail() throws Exception {

        String[] paths = deferPathsFromMethod();
        boolean res = Validator.validate(paths[0], paths[1]);
        Assertions.assertTrue(res);
    }

    @Test
    void val_localSchema_MultiValidationError() throws Exception {

        String[] paths = deferPathsFromMethod();
        boolean res = Validator.validate(paths[0], paths[1]);
        Assertions.assertFalse(res);
    }

    private static String[] deferPathsFromMethod(){
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement elem = stacktrace[2]; //Stacktrace-element of the calling method (= test method)
        String methodName = elem.getMethodName();

        String content = Path.of("src/test/resources/" + methodName + "/content.json").toString();
        String schema = Path.of("src/test/resources/" + methodName + "/schema.json").toString();

        return new String[]{content, schema};
    }
}
