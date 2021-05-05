package ch.so.agi.sql2json.test;

import java.nio.file.Path;

public class Util {
    public static Path deferTestResourcesPathFromCallingMethod(){
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement elem = stacktrace[2]; //Stacktrace-element of the calling method (= test method)
        String methodName = elem.getMethodName();

        return Path.of("src/test/resources/" + methodName);
    }
}
