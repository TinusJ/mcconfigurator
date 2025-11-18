package com.tinusj.mcconfigurator;

import com.moandjiezana.toml.Toml;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TomlConfigParser implements ConfigParser {

    @Override
    public ConfigType getSupportedType() {
        return ConfigType.TOML;
    }

    @Override
    public ConfigNode parse(Path filePath) throws Exception {
        Toml toml = new Toml().read(filePath.toFile());
        return convertTomlToNode("root", toml.toMap());
    }

    private ConfigNode convertTomlToNode(String name, Object value) {
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            List<ConfigNode> children = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                children.add(convertTomlToNode(entry.getKey(), entry.getValue()));
            }
            return ConfigNode.branch(name, children);
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            List<ConfigNode> children = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                children.add(convertTomlToNode("[" + i + "]", list.get(i)));
            }
            return ConfigNode.branch(name, children);
        } else {
            return ConfigNode.leaf(name, value);
        }
    }
}
