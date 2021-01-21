package ch.so.agi.sql2json.exception;

import java.text.MessageFormat;

public class TrafoException extends RuntimeException {

    private Type type;

    public TrafoException(String msg){ super(msg); }

    public TrafoException(Exception wrapped){ super(wrapped); }

    public TrafoException(String msg, Object... msgValues){
        super(MessageFormat.format(msg, msgValues));
    }

    public TrafoException(Type type, String msg, Object... msgValues){

        super(MessageFormat.format(msg, msgValues));
        this.type = type;
    }

    public Type getType(){
        return type;
    }

    public enum Type {
        NO_ROWS, COLUMNTYPE_UNKNOWN
    }
}


