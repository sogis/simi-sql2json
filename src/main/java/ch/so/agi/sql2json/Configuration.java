package ch.so.agi.sql2json;

import ch.so.agi.sql2json.exception.TrafoException;
import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
    public static final String JSON_SCHEMA = "s";
    private static final String HELP = "h";
    private static final String VERSION = "v";

    private static Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Configuration conf;

    private HashMap<String, ConfigurationEntry> confMap;
    private boolean helpPrinted;

    public static void createConfig4Args(String[] args){
        conf = new Configuration(args);
    }

    public static String valueForKey(String confName){

        ConfigurationEntry entry = conf.confMap.get(confName);
        String value = resolveValue(entry);

        return value;
    }

    private Configuration(String[] args){

        createConfigMapWithoutValues();

        Options opt = optionsFromConfMap();

        CommandLineParser parser = new DefaultParser();
        CommandLine para = null;

        try {
            para = parser.parse(opt, args);
        } catch (ParseException e) {
            throw new TrafoException(e);
        }

        if(para.hasOption(HELP) || para.hasOption(VERSION)){
            showHelp(opt);
            return;
        }

        setCommandLineValues(para);
    }

    private void showHelp(Options opt){
        String version = this.getClass().getPackage().getImplementationVersion();

        HelpFormatter formatter = new HelpFormatter();

        String sep = "**************************************************************************";

        formatter.printHelp(
                MessageFormat.format("\n{0}\njava -jar sql2json.jar [options]", sep),
                "options:",
                opt,
                MessageFormat.format("---------\nversion: {0}\n{1}", version, sep),
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

    private void createConfigMapWithoutValues(){

        this.confMap = new HashMap<String, ConfigurationEntry>();

        addEntry(TEMPLATE_PATH, "SqlTrafo_Templatepath",
                "Absoluter Dateipfad zum zu verarbeitenden Template. Bsp: opt/user/trafo/wms/template.json");
        addEntry(OUTPUT_PATH, "SqlTrafo_Outputpath",
                "Absoluter Dateipfad des output config.json. Bsp: opt/user/trafo/wms/config.json");
        addEntry(DB_CONNECTION, "SqlTrafo_DbConnection",
                "JDBC Connection-URL zur abzufragenden DB. Aufbau: jdbc:postgresql://host:port/database");
        addEntry(DB_USER, "SqlTrafo_DbUser",
                "Benutzername für die DB-Verbindung");
        addEntry(DB_PASSWORD, "SqlTrafo_DbPassword",
                "Passwort für die DB-Verbindung");
        addEntry(JSON_SCHEMA, "SqlTrafo_JsonSchema",
                "Optionaler Pfad zum Json-Schema. Als http(s) oder Dateipfad angeben, sofern der output gegen ein Schema validiert werden soll");

        String desc = "Ausgabe von Version und Hilfetext zum Commandline-Tool sql2json";
        addEntry(HELP, null, desc);
        addEntry(VERSION, null, desc);
    }

    private void addEntry(String cmdLineParam, String envVarName, String description){

        Option o = new Option(cmdLineParam, envVarName != null, description);

        ConfigurationEntry c = new ConfigurationEntry();
        c.setCommandLineOption(o);
        c.setEnvVariableName(envVarName);

        confMap.put(cmdLineParam, c);
    }

    private void setCommandLineValues(CommandLine para) {
        for (ConfigurationEntry ce : confMap.values()){
            String key = ce.getCommandLineOption().getOpt();
            ce.setCommandLineValue(para.getOptionValue(key));
        }
    }

    public static void assertComplete(){ conf._assertComplete(); }

    private void _assertComplete(){

        List<String> missingParams = new ArrayList<>();

        for (ConfigurationEntry ce : confMap.values()){

            String parName = ce.getCommandLineOption().getOpt();
            if(HELP.equals(parName) || VERSION.equals(parName) || !(ce.getCommandLineOption().isRequired()))
                continue;

            String val = resolveValue(ce);

            if(val == null) {
                String errMsg = MessageFormat.format(
                        "Either set param -{0} on commandline, or define env variable {1}",
                        ce.getCommandLineOption().getOpt(),
                        ce.getEnvVariableName());

                missingParams.add(errMsg);
            }
        }

        if(missingParams.size() > 0) {
            Options opt = optionsFromConfMap();
            showHelp(opt);

            String errMsg = "Missing configurations:\n" + String.join(" |\n", missingParams);
            throw new TrafoException(errMsg);
        }
    }

    private static String resolveValue(ConfigurationEntry confEntry){

        String key = confEntry.getCommandLineOption().getOpt();
        String val = confEntry.getValue();

        if(val != null && val.length() > 0) // --> hit from value cache in ConfigurationEntry
            return val;

        val = confEntry.getCommandLineValue();

        if(val != null && val.length() > 0) {
            log.info("Using param value from commandline for -{}", key);
        }
        else {
            String envVarName = confEntry.getEnvVariableName();
            val = System.getenv(envVarName);

            if (val != null && val.length() > 0) {
                log.info("Using param value from env variable {} for -{}", envVarName, key);
            }
        }

        confEntry.setValue(val);

        return val;
    }

    public static boolean helpPrinted(){
        return conf.helpPrinted;
    }
}
