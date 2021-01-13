package ch.so.agi.sql2json;

import java.text.MessageFormat;

public class TrafoException extends RuntimeException {
    public TrafoException(String msg){ super(msg); }

    public TrafoException(String msg, Object... msgValues){
        super(MessageFormat.format(msg, msgValues));
    }
}
