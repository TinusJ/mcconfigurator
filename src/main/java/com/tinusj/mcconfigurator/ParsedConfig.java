package com.tinusj.mcconfigurator;

public class ParsedConfig {
    private final String fileName;
    private final ConfigType configType;
    private final ConfigNode rootNode;
    private final String errorMessage;

    private ParsedConfig(String fileName, ConfigType configType, ConfigNode rootNode, String errorMessage) {
        this.fileName = fileName;
        this.configType = configType;
        this.rootNode = rootNode;
        this.errorMessage = errorMessage;
    }

    public static ParsedConfig success(String fileName, ConfigType configType, ConfigNode rootNode) {
        return new ParsedConfig(fileName, configType, rootNode, null);
    }

    public static ParsedConfig error(String fileName, ConfigType configType, String errorMessage) {
        return new ParsedConfig(fileName, configType, null, errorMessage);
    }

    public String getFileName() {
        return fileName;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public ConfigNode getRootNode() {
        return rootNode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null;
    }
}
