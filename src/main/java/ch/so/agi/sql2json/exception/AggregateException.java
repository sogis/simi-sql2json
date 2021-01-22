package ch.so.agi.sql2json.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;

public class AggregateException extends TrafoException {

    private ArrayList<TrafoException> tagExceptions;

    public AggregateException(ArrayList<TrafoException> tagExceptions){
        super(ExType.TAG_EX_COLLECTION, "Tag processing produced {0} errors.", tagExceptions.size());
        this.tagExceptions = tagExceptions;
    }

    @Override
    public void printStackTrace(){
        super.printStackTrace();

        int num = 1;
        for (TrafoException te : tagExceptions) {
            System.err.println(MessageFormat.format("TagException {0}:", num));
            te.printStackTrace();

            num++;
        }
    }

    public TrafoException firstException(){
        return tagExceptions.get(0);
    }
}
