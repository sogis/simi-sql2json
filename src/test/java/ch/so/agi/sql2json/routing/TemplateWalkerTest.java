package ch.so.agi.sql2json.routing;

import ch.so.agi.sql2json.Application;
import ch.so.agi.sql2json.Configuration;
import ch.so.agi.sql2json.TextFileReader;
import ch.so.agi.sql2json.exception.AggregateException;
import ch.so.agi.sql2json.exception.ExType;
import ch.so.agi.sql2json.exception.TrafoException;
import ch.so.agi.sql2json.tag.Tag;
import ch.so.agi.sql2json.tag.JsonType;
import ch.so.agi.sql2json.test.Util;
import com.fasterxml.jackson.databind.JsonNode;
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
      "-t", "will-be-replaced",
      "-o", "dummy",
      "-c", "jdbc:postgresql://localhost/postgres",
      "-u", "postgres",
      "-p", "postgres",
    };

    private static final String LIST_MARKER = "§list_ok§";
    private static final String SET_MARKER = "§set_ok§";
    private static final String OBJ_MARKER = "§mark§";

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel","info");
    }

    @Test
    void withoutTag_Identical_OK() throws Exception {

        initConfigForTest();
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

        initConfigForTest();
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
        execAndAssertContains(Tag.MARKER_VALUE);
    }

    @Test
    void list_OfJsonb_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType(OBJ_MARKER, JsonType.JSON_ELEMENT);
    }

    @Test
    void list_Nested_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType(OBJ_MARKER, JsonType.JSON_ELEMENT);
    }


    @Test
    void list_OfJson_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType(OBJ_MARKER, JsonType.JSON_ELEMENT);
    }

    @Test
    void list_OfString_OK() throws Exception {

        initConfigForTest();
        execAndAssertContains(LIST_MARKER);
    }

    @Test
    void list_WithNullValues_OK() throws Exception {

        initConfigForTest();
        execAndAssertContains(LIST_MARKER);
    }

    @Test
    void list_NoRows_ThrowsCorrect() throws Exception {

        initConfigForTest();
        execAndAssertRaises(ExType.NO_ROWS);
    }

    @Test
    void list_UnknownColumnType_ThrowsCorrect() throws Exception {

        initConfigForTest();
        execAndAssertRaises(ExType.VAL_COLUMNTYPE_UNKNOWN);
    }

    @Test
    void set_OfJson_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType(OBJ_MARKER, JsonType.JSON_ELEMENT);
    }

    @Test
    void set_OfString_OK() throws Exception {

        initConfigForTest();
        execAndAssertContains(SET_MARKER);
    }

    @Test
    void set_MissingColumn_ThrowsCorrect() throws Exception {

        initConfigForTest();
        execAndAssertRaises(ExType.MISSING_COLUMNS);
    }

    @Test
    void set_InvalidKeyColumnType_ThrowsCorrect() throws Exception {

        initConfigForTest();
        execAndAssertRaises(ExType.WRONG_KEY_COLUMNTYPE);
    }

    @Test
    void set_DuplicateKey_ThrowsCorrect() throws Exception {

        initConfigForTest();
        execAndAssertRaises(ExType.SET_HAS_DUPLICATES);
    }

    @Test
    void elem_OfString_OK() throws Exception {

        initConfigForTest();
        execAndAssertContains("§elem_ok§");
    }

    @Test
    void elem_OfString_Remote_OK() throws Exception {

        String[] args = Arrays.copyOf(CONFIG_TEMPLATE, CONFIG_TEMPLATE.length);

        args[TEMPLATE_IDX] = "https://raw.githubusercontent.com/sogis/simi-sql2json/main/src/test/resources/elem_OfString_Remote_OK/template.json";

        Configuration.createConfig4Args(args);
        Configuration.assertComplete();

        execAndAssertContains("§elem_ok§");
    }

    @Test
    void elem_OfJson_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType(OBJ_MARKER, JsonType.JSON_ELEMENT);
    }

    @Test
    void elem_OfNull_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType("§ident§", null);
    }

    @Test
    void elem_OfNumber_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType("§ident§", JsonType.NUMBER);
    }

    @Test
    void elem_OfBoolean_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType("§ident§", JsonType.BOOLEAN);
    }

    @Test
    void elem_OfStringWithSpecialChars_OK() throws Exception {

        initConfigForTest();
        execAndCheckValueType("§ident§", JsonType.STRING);
    }

    @Test
    void config_NoArgs_PrintsHelp() {
        Configuration.createConfig4Args(null);
        assertTrue(Configuration.helpPrinted());
    }

    @Test
    void mixedTags_WithErrors_WritesToEnd() throws Exception {

        initConfigForTest();

        String template = loadTestJson();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertThrows(AggregateException.class, () -> {TemplateWalker.walkTemplate(template, output);});

        String resJson = readAll(output);
        assertTrue(resJson.contains("§last_element§"), "Resulting json does not contain the marker string");
        assertDoesNotThrow(() -> {mapper.readTree(resJson);});
    }

    private void execAndAssertRaises(ExType exType) throws Exception{
        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            readAll(output);
        }
        catch(AggregateException t){
            if(!exType.equals(t.firstException().getType()))
                handle(t, output);
        }
        catch(Exception e){
            handle(e, output);
        }
    }

    private void execAndCheckValueType(String jsonObjectKey, JsonType valueType) throws Exception{
        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            String resJson = readAll(output);

            JsonNode root = mapper.readTree(resJson); // the tested json object
            JsonNode inner = root.findValue(jsonObjectKey);

            if(valueType == null) { // for json null...
                if(!inner.isNull())
                    throw new TrafoException("Expected 'null' as value for key {0}", jsonObjectKey);
            }
            else if(valueType == JsonType.JSON_ELEMENT){
                boolean isElem = inner.isArray() || inner.isObject();
                if(!isElem)
                    throw new TrafoException("Expected array or object as value for key {0}", jsonObjectKey);
            }
            else if(valueType == JsonType.STRING){
                if(!inner.isTextual())
                    throw new TrafoException("Expected string as value for key {0}", jsonObjectKey);
            }
            else if(valueType == JsonType.NUMBER){
                if(!inner.isInt())
                    throw new TrafoException("Expected integer as value for key {0}", jsonObjectKey);
            }
            else if(valueType == JsonType.BOOLEAN){
                if(!inner.isBoolean())
                    throw new TrafoException("Expected boolean as value for key {0}", jsonObjectKey);
            }
            else {
                throw new TrafoException("Method execAndCheckValueType(...) does not handle JsonType {0}", valueType);
            }
        }
        catch(Exception e){
            handle(e, output);
        }
    }

    private void execAndAssertContains(String outputMarker) throws Exception{
        execAndAssertContains(outputMarker, "");
    }

    private void execAndAssertContains(String outputMarker, String pathToSetOrArray) throws Exception{
        String template = loadTestJson();
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();

            TemplateWalker.walkTemplate(template, output);
            String resJson = readAll(output);

            assertTrue(resJson.contains(outputMarker), "Resulting json does not contain the marker string");
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

        TextFileReader file = TextFileReader.create(Configuration.valueForKey(Configuration.TEMPLATE_PATH));
        String json = file.readContentToString();

        return json;
    }

    private static void initConfigForTest(){

        Path base = Util.deferTestResourcesAbsPathFromCallingMethod(2);
        Path p = base.resolve("template.json");

        String[] args = Arrays.copyOf(CONFIG_TEMPLATE, CONFIG_TEMPLATE.length);

        args[TEMPLATE_IDX] = p.toAbsolutePath().toString();

        Configuration.createConfig4Args(args);
        Configuration.assertComplete();
    }
}
