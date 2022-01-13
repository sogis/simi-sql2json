package ch.so.agi.sql2json;

import ch.so.agi.sql2json.exception.TrafoException;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * Abstraction for reading a local or remote TextFile.
 * The files encoding must be UTF-8.
 */
public class TextFileReader {

    private URI fileLocation;

    private TextFileReader(URI fileLocation){

        URI candidate = addSchemePrefixIfMissing(fileLocation, "file");
        assertAbsoluteURI(candidate);
        assertAbsolutePathInURI(fileLocation);

        this.fileLocation = candidate;
    }

    private static void assertAbsoluteURI(URI uri){
        if(!uri.isAbsolute()){
            String msg = MessageFormat.format("URI must be absolute (= qualified with scheme). Given URI: {0}", uri);
            throw new TrafoException(msg);
        }
    }

    private static URI addSchemePrefixIfMissing(URI uri, String schemeName){
        URI res = null;

        if(uri.getScheme() == null)
            try{
                res = new URI(schemeName, uri.getSchemeSpecificPart(), null);
            }
            catch (Exception ex){
                throw new RuntimeException(ex);
            }
        else{
            res = uri;
        }

        return res;
    }

    private static void assertAbsolutePathInURI(URI fileLocation){

        Path pathInUri = Paths.get(fileLocation.getPath());

        if(!pathInUri.isAbsolute()){
            String msg = MessageFormat.format("The URIs path part must be absolute. Violating URI: {0}", fileLocation.toString());
            throw new TrafoException(msg);
        }
    }

    /**
     * Creates a new TextFileReader from the given path.
     * @param path String representing the path. Either a URI starting with http:, file:, ... or a OS dependent UNC file path.
     * @return Constructed TextFileReader instance.
     */
    public static TextFileReader create(String path){
        URI uri = URI.create(path);
        return TextFileReader.create(uri);
    }

    /**
     * Creates a new TextFileReader from the given URI.
     */
    public static TextFileReader create(URI path) {
        return new TextFileReader(path);
    }

    /**
     * Constructs a TextFileReader for the sibling File located in the
     * same location (Path, Folder) as the mainFile
     * @param mainFile
     * @param siblingFileName
     * @return
     */
    public static TextFileReader create(URI mainFile, String siblingFileName){

        URI sibling = mainFile.resolve(siblingFileName);

        return TextFileReader.create(sibling);
    }

    public URI fileLocation(){
        return fileLocation;
    }

    /**
     * Reads the File content and returns it.
     */
    public String readContentToString() {
        String res = null;

        try (InputStream ins = fileLocation.toURL().openStream()) {
            byte[] bytes = ins.readAllBytes();
            res = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}