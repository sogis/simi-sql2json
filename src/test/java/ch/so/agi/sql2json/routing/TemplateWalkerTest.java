package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.exception.TrafoException;
import ch.so.agi.sql2json.tag.BaseTag;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateWalkerTest {

    private ObjectMapper mapper = new ObjectMapper();

    private static final int TEMPLATE_IDX = 1;
    private static final String[] CONFIG_TEMPLATE = {
      "-t", "fuu",
      "-c", "jdbc:postgresql://localhost/postgres",
      "-u", "postgres",
      "-p", "postgres",
      "-l", "debug"
    };



    public static final String TEMPLATE_PATH = "t";
    public static final String OUTPUT_PATH = "o";
    public static final String DB_CONNECTION = "c";
    public static final String DB_USER = "u";
    public static final String DB_PASSWORD = "p";
    public static final String LOG_LEVEL = "l";
    private static final String HELP = "h";
    private static final String VERSION = "v";

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

        initConfigForTest();
        execAndValidate(BaseTag.MARKER_VALUE);
/*
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
        }*/
    }

    @Test
    void list_Jsonb_OK() throws Exception {

        initConfigForTest();
        execAndValidate("§list_ok§");
    }

    @Test
    void list_Json_OK() throws Exception {

        initConfigForTest();
        execAndValidate("§list_ok§");
    }

    @Test
    void list_NoRows_ThrowsCorrect() throws Exception {

        initConfigForTest();
        execAndAssertRaises(TrafoException.Type.NO_ROWS);
    }

    @Test
    void list_UnknownColumnType_ThrowsCorrect() throws Exception {

        initConfigForTest();
        execAndAssertRaises(TrafoException.Type.COLUMNTYPE_UNKNOWN);
    }

    private String execAndAssertRaises(TrafoException.Type exType) throws Exception{
        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        String resJson = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            resJson = readAll(output);
        }
        catch(TrafoException t){
            if(!exType.equals(t.getType()))
                handle(t, output);
        }
        catch(Exception e){
            handle(e, output);
        }

        return resJson;
    }

    private String execAndValidate(String outputMarker) throws Exception{
        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        String res = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            String resJson = readAll(output);

            assertTrue(resJson.contains(outputMarker), "Resulting json does not contain the marker string");
            assertDoesNotThrow(() -> {mapper.readTree(resJson);});

            res = resJson;
        }
        catch(Exception e){
            handle(e, output);
        }

        return res;
    }

    /*
    Tests:
    - Alle primitiven Einzelwerte
        Hier: Mapping auf den entsprechenden Json-Typ untersuchen und verifizieren
    - map aus Tabelle
    - Array aus Tabelle
    - Query ohne result
    - Query mit rescolumn unbekannten typs
    - Query mit null-Werten
     */

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

        String path = Configuration.valueForKey(Configuration.TEMPLATE_PATH);

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

    private static void initConfigForTest(){

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement elem = stacktrace[2]; //Stacktrace-element of the calling method (= test method)
        String methodName = elem.getMethodName();

        Path p = Path.of("src/test/resources/" + methodName + "/template.json");

        String[] args = Arrays.copyOf(CONFIG_TEMPLATE, CONFIG_TEMPLATE.length);

        args[TEMPLATE_IDX] = p.toAbsolutePath().toString();

        Configuration.createConfig4Args(args);
    }




}
