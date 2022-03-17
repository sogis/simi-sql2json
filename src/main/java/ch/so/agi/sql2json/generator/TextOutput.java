package ch.so.agi.sql2json.generator;

import ch.so.agi.sql2json.exception.TrafoException;

import java.io.File;
import java.io.OutputStream;

/**
 * Textueller output des sql2json.
 *
 * Meist eine json-Datei, deren Inhalt über einen FileOutputStream
 * geschrieben wird.
 */
public class TextOutput {

    private File textFile;
    private OutputFormat format;

    public TextOutput(OutputFormat format, File textFile){
        this.format = format;
        this.textFile = textFile;
    }

    public File getTextFile() {
        return textFile;
    }

    public OutputFormat getFormat() {
        return format;
    }
}
