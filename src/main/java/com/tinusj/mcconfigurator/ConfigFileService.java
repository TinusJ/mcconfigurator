package com.tinusj.mcconfigurator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigFileService {

    private final Map<ConfigType, ConfigParser> parsers;

    public ConfigFileService(List<ConfigParser> parserList) {
        this.parsers = new HashMap<>();
        for (ConfigParser parser : parserList) {
            parsers.put(parser.getSupportedType(), parser);
        }
    }

    public List<ParsedConfig> loadConfigs(Path directory) {
        List<ParsedConfig> results = new ArrayList<>();

        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return results;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String fileName = file.getFileName().toString();
                    ConfigType type = ConfigType.fromFileName(fileName);
                    
                    if (type != null) {
                        ParsedConfig config = parseFile(file, fileName, type);
                        results.add(config);
                    }
                }
            }
        } catch (IOException e) {
            // Log or handle directory reading error
        }

        return results;
    }

    private ParsedConfig parseFile(Path file, String fileName, ConfigType type) {
        ConfigParser parser = parsers.get(type);
        if (parser == null) {
            return ParsedConfig.error(fileName, type, "No parser available for type: " + type);
        }

        try {
            ConfigNode rootNode = parser.parse(file);
            return ParsedConfig.success(fileName, type, rootNode);
        } catch (Exception e) {
            return ParsedConfig.error(fileName, type, "Parse error: " + e.getMessage());
        }
    }
}
