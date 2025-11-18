package com.tinusj.mcconfigurator;

public enum ConfigType {
    JSON(".json"),
    TOML(".toml"),
    CFG(".cfg");

    private final String extension;

    ConfigType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static ConfigType fromFileName(String fileName) {
        String lowerName = fileName.toLowerCase();
        for (ConfigType type : values()) {
            if (lowerName.endsWith(type.extension)) {
                return type;
            }
        }
        return null;
    }
}
