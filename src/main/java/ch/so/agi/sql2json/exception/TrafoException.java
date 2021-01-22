package ch.so.agi.sql2json.exception;

import java.text.MessageFormat;

public class TrafoException extends RuntimeException {

    private ExType type;

    public TrafoException(String msg){ super(msg); }

    public TrafoException(Exception wrapped){ super(wrapped); }

    public TrafoException(String msg, Object... msgValues){
        super(MessageFormat.format(msg, msgValues));
    }

    public TrafoException(Exception inner, String msg, Object... msgValues){
        super(MessageFormat.format(msg, msgValues), inner);
    }

    public TrafoException(ExType type, String msg, Object... msgValues){
        super(MessageFormat.format(msg, msgValues));
        this.type = type;
    }

    public ExType getType(){
        return type;
    }
}


