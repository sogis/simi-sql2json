package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.TrafoException;
import ch.so.agi.sql2json.tag.BaseTag;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateWalkerTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void withoutTag_Identical_OK() throws Exception {

        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            String resJson = readAll(output);

            assertEquals(mapper.readTree(template), mapper.readTree(resJson));
        }
        catch(Exception e){
            handle(e, output);
        }
    }

    @Test
    void withoutTag_CandiateAfterObjValue_OK() throws Exception {
        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            String resJson = readAll(output);

            assertEquals(mapper.readTree(template), mapper.readTree(resJson));
        }
        catch(Exception e){
            handle(e, output);
        }
    }

    @Test
    void withTag_Replacing_OK() throws Exception {
        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            String resJson = readAll(output);

            assertTrue(resJson.contains(BaseTag.MARKER_VALUE), "Resulting json does not contain the marker string");
            assertDoesNotThrow(() -> {mapper.readTree(resJson);});
        }
        catch(Exception e){
            handle(e, output);
        }
    }

    private static void handle(Exception e, ByteArrayOutputStream out) throws Exception {
        System.err.println("Error occured. Written json-output before error: ");
        try{
            String json = readAll(out);
            System.err.println(json);
        }
        catch(Exception inner){}

        throw e;
    }

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
