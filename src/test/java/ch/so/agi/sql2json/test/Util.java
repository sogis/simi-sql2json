package ch.so.agi.sql2json.test;

import java.nio.file.Path;

public class Util {

    public static Path deferTestResourcesPathFromCallingMethod(Integer callDepth){

        if(callDepth == null)
            callDepth = new Integer(1);

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement elem = stacktrace[callDepth.intValue() + 1]; //Stacktrace-element of the calling method (= test method)
        String methodName = elem.getMethodName();

        return Path.of("src/test/resources/" + methodName);
    }
}
