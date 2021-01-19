package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.TrafoException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;

public class TemplateWalkerTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void jsonWithoutTag_Identical() throws Exception {

        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            String resJson = readAll(output);

            assertEquals(mapper.readTree(template), mapper.readTree(resJson));
        }
        catch(Exception e){
            System.err.println("Error occured. Written json-output before error: ");
            try{
                String json = readAll(output);
                System.err.println(json);
            }
            catch(Exception inner){}

            throw e;
        }
    }

    //test tag nach array-value

    private static String readAll(ByteArrayOutputStream outStream){
        return new String(outStream.toByteArray());
    }

    private static String loadTestJson(){

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement elem = stacktrace[2]; //Stacktrace-element of the calling method (= test method)
        String methodName = elem.getMethodName();

        String path = "src/test/resources/" + methodName + ".json";

        String json = null;
        try (FileInputStream fis = new FileInputStream(path)) {
            byte[] data = fis.readAllBytes();
            json = new String(data);
        }
        catch(Exception e){
            throw new TrafoException(e);
        }
        return json;
    }




}
