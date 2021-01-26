package ch.so.agi.sql2json;

import org.apache.commons.cli.Option;

public class ConfigurationEntry {
    private Option commandLineOption;
    private String envVariableName;
    private String commandLineValue;
    private String value;

    public Option getCommandLineOption() {
        return commandLineOption;
    }

    public void setCommandLineOption(Option commandLineOption) {
        this.commandLineOption = commandLineOption;
    }

    public String getEnvVariableName() {
        return envVariableName;
    }

    public void setEnvVariableName(String envVariableName) {
        this.envVariableName = envVariableName;
    }

    public String getCommandLineValue() {
        return commandLineValue;
    }

    public void setCommandLineValue(String commandLineValue) {
        this.commandLineValue = commandLineValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
