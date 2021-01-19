package ch.so.agi.sql2json.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class Logging {

    public static void initToLogLevel(String levelName){

        Level level = levelForName(levelName);

        ConfigurationBuilder<BuiltConfiguration> builder
                = ConfigurationBuilderFactory.newConfigurationBuilder();

        //"channel" console
        AppenderComponentBuilder console
                = builder.newAppender("stdout", "Console");

        builder.add(console);

        // layout
        LayoutComponentBuilder standard
                = builder.newLayout("PatternLayout");
        standard.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");

        console.add(standard);

        // root logger
        Level l = levelForName(levelName);
        boolean fallBackLevel = false;
        if(l == null){
            l = Level.INFO;
            fallBackLevel = true;
        }

        RootLoggerComponentBuilder rootLogger
                = builder.newRootLogger(l);
        rootLogger.add(builder.newAppenderRef("stdout"));

        builder.add(rootLogger);

        // initialize logging with these settings
        Configurator.initialize(builder.build());

        if(fallBackLevel){
            Logger log = LogManager.getLogger(Logging.class);
            log.warn("Given log level string '{}' was not in {}. Falling back to default log level {}", levelName, Level.values(), Level.INFO);
        }
    }

    private static Level levelForName(String name){
        if(name == null || name.length()==0)
            return null;

        return Level.getLevel(name.toUpperCase());
    }
}
