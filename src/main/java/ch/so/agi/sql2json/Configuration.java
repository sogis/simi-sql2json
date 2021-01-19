package ch.so.agi.sql2json;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Configuration {

    public static final String TEMPLATE_PATH = "t";
    public static final String OUTPUT_PATH = "o";
    public static final String DB_CONNECTION = "c";
    public static final String DB_USER = "u";
    public static final String DB_PASSWORD = "p";
    public static final String LOG_LEVEL = "l";
    private static final String HELP = "h";
    private static final String VERSION = "v";

    private static Logger log = LogManager.getLogger(Configuration.class);

    private HashMap<String, ConfigurationEntry> confMap;
    private String errorMessage;
    private boolean helpPrinted;

    public static Configuration createConfig4Args(String[] args){
        return new Configuration(args);
    }

    public Configuration(String[] args){
        Configurator.setRootLevel(Level.INFO);

        createConfigMap();

        Options opt = optionsFromConfMap();

        String desc = "Ausgabe von Version und Hilfetext zum Commandline-Tool sql2json";
        opt.addOption(HELP, false, desc);
        opt.addOption(VERSION, false, desc);

        CommandLineParser parser = new DefaultParser();
        CommandLine para = null;
        try {
            para = parser.parse(opt, args);
        } catch (ParseException e) {
            log.error("Error parsing commandline params", e);
        }

        if(para.hasOption(HELP) || para.hasOption(VERSION)){
            showHelp(opt);
        }

        setConfigValues(para);
    }

    private void showHelp(Options opt){
        String version = this.getClass().getPackage().getImplementationVersion();

        HelpFormatter formatter = new HelpFormatter();
        //formatter.printHelp( "Version: " + version + " \nUsage: java -jar sql2json.jar [options]. \nOptions:", opt);
        formatter.printHelp(
                "java -jar sql2json.jar [options]",
                "options:",
                opt,
                "**************************************************************************\nversion: " + version,
                false);

        helpPrinted = true;
        return;
    }

    private Options optionsFromConfMap(){
        Options opt = new Options();

        for (ConfigurationEntry ce : confMap.values()){
            opt.addOption(ce.getCommandLineOption());
        }

        return opt;
    }

    private void createConfigMap(){

        this.confMap = new HashMap<String, ConfigurationEntry>();

        addEntry(TEMPLATE_PATH, "SqlTrafo_Templatepath", "Absoluter Dateipfad zum zu verarbeitenden Template. Bsp: opt/user/trafo/wms/template.json");
        addEntry(OUTPUT_PATH, "SqlTrafo_Outputpath", "Absoluter pfad und Dateiname des output config.json. Bsp: opt/user/trafo/wms/config.json");
        addEntry(DB_CONNECTION, "SqlTrafo_DbConnection", "JDBC Connection-URL zur abzufragenden DB. Aufbau: jdbc:postgresql://host:port/database");
        addEntry(DB_USER, "SqlTrafo_DbUser", "Benutzername für die DB-Verbindung");
        addEntry(DB_PASSWORD, "SqlTrafo_DbPassword", "Passwort für die DB-Verbindung");
        addEntry(LOG_LEVEL, "SqlTrafo_LogLevel", "Logging-Level: debug, info, warn oder error");
    }

    private void addEntry(String cmdLineParam, String envVarName, String description){
        Option o = new Option(cmdLineParam, true, description);

        ConfigurationEntry c = new ConfigurationEntry();
        c.setCommandLineOption(o);
        c.setEnvVariableName(envVarName);

        confMap.put(cmdLineParam, c);
    }

    private void setConfigValues(CommandLine para){

        List<String> missingParams = new ArrayList<>();

        for (ConfigurationEntry ce : confMap.values()){

            String key = ce.getCommandLineOption().getOpt();
            String val = para.getOptionValue(key);

            if (val != null && val.length() > 0){
                log.info("Using param value from commandline for -{}", key);
            }
            else{
                String envVarName = ce.getEnvVariableName();
                val = System.getenv(envVarName);

                if (val != null && val.length() > 0) {
                    log.info("Using param value from env variable {} for -{}", envVarName, key);
                }
                else{
                    String errMsg = MessageFormat.format(
                            "Either set param -{0} on commandline, or define env variable {1}",
                            key,
                            envVarName);

                    missingParams.add(errMsg);
                }
            }

            ce.setValue(val);
        }

        if(missingParams.size() > 0) {
            this.errorMessage = "Missing configurations:\n" + String.join(" |\n", missingParams);
        }
    }

    public void assertComplete(){
        if(this.errorMessage != null){
            Options opt = optionsFromConfMap();
            showHelp(opt);

            throw new TrafoException(errorMessage);
        }
    }

    public String getConfigValue(String paramName){

        ConfigurationEntry entry = confMap.get(paramName);
        return entry.getValue();
    }

    public boolean helpPrinted(){
        return helpPrinted;
    }
}
