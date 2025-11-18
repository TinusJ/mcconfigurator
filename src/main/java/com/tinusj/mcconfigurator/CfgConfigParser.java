package com.tinusj.mcconfigurator;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class CfgConfigParser implements ConfigParser {

    @Override
    public ConfigType getSupportedType() {
        return ConfigType.CFG;
    }

    @Override
    public ConfigNode parse(Path filePath) throws Exception {
        List<ConfigNode> children = new ArrayList<>();
        ConfigNode currentSection = null;
        String currentSectionName = null;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                    continue;
                }
                
                // Section header
                if (line.startsWith("[") && line.endsWith("]")) {
                    if (currentSection != null) {
                        children.add(currentSection);
                    }
                    currentSectionName = line.substring(1, line.length() - 1);
                    currentSection = ConfigNode.branch(currentSectionName);
                } else if (line.contains("=")) {
                    // Key-value pair
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";
                    
                    ConfigNode kvNode = ConfigNode.leaf(key, value);
                    if (currentSection != null) {
                        currentSection.addChild(kvNode);
                    } else {
                        children.add(kvNode);
                    }
                }
            }
            
            if (currentSection != null) {
                children.add(currentSection);
            }
        }
        
        return ConfigNode.branch("root", children);
    }
}
