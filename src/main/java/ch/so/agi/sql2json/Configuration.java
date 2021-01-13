package ch.so.agi.sql2json;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;

public class Configuration {

    private static Logger log = LoggerFactory.getLogger(Configuration.class);

    private HashMap<String, ConfigurationEntry> confMap;
    private CommandLine para;

    public static Configuration createConfig4Args(String[] args){
        return new Configuration(args);
    }

    public Configuration(String[] args){

        createConfigMap();
        Options opt = optionsFromConfMap();

        CommandLineParser parser = new DefaultParser();
        CommandLine para = null;
        try {
            para = parser.parse(opt, args);
        } catch (ParseException e) {
            log.info(e.getMessage());
        }

        this.para = para;
        
        /*
        EVtl. Umschreiben:
        - ?Options zuerst erzeugen
        - dann Wert abholen
        - dann Wert in confMap setzen
         */
    }

    private Options optionsFromConfMap(){
        Options opt = new Options();

        for (ConfigurationEntry ce : confMap.values()){
            opt.addOption(ce.getCommandLineOption());
        }

        //add help option
        opt.addOption("h", false, "Ausgabe des Hilfetexts zum Commandline-Tool sql2json");

        return opt;
    }

    private void createConfigMap(){

        this.confMap = new HashMap<String, ConfigurationEntry>();

        addEntry("t", "SqlTrafo_Templatepath", "Absoluter Dateipfad zum zu verarbeitenden Template. Bsp: opt/user/trafo/wms/template.json");
        addEntry("o", "SqlTrafo_Outputpath", "Absoluter pfad und Dateiname des output config.json. Bsp: opt/user/trafo/wms/config.json");
        addEntry("c", "SqlTrafo_DbConnection", "JDBC Connection-URL zur abzufragenden DB. Aufbau: jdbc:postgresql://host:port/database");
        addEntry("u", "SqlTrafo_DbUser", "Benutzername für die DB-Verbindung");
        addEntry("p", "SqlTrafo_DbPassword", "Passwort für die DB-Verbindung");
        addEntry("l", "SqlTrafo_LogLevel", "Logging-Level: Silent, Info, Warn(ing), Debug");
    }

    private void addEntry(String cmdLineParam, String envVarName, String description){
        Option o = new Option(cmdLineParam, description);

        ConfigurationEntry c = new ConfigurationEntry();
        c.setCommandLineOption(o);
        c.setEnvVariableName(envVarName);

        confMap.put(cmdLineParam, c);
    }

    private String getConfigValue(String paramName){

        String val = para.getOptionValue(paramName);

        if (val == null || val.length() == 0){
            String envVarName = confMap.get(paramName).getEnvVariableName();
            val = System.getenv(envVarName);

            if (val == null || val.length() == 0) {
                throw new RuntimeException(MessageFormat.format(
                        "Missing configuration: Either set param -{0} on commandline, or define env variable {1}",
                        paramName,
                        envVarName
                ));
            }
            else{
                log.info("Using param value from env variable {0} for {1}", envVarName, paramName);
            }
        }
        else{
            log.info("Using param value from commandline for {0}", paramName);
        }
        return val;
    }
}
