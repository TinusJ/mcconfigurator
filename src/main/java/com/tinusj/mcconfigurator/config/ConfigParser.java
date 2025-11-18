package com.tinusj.mcconfigurator.config;

import java.nio.file.Path;

public interface ConfigParser {
    ConfigType getSupportedType();
    ConfigNode parse(Path filePath) throws Exception;
}
