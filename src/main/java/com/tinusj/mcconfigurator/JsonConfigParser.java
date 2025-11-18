package com.tinusj.mcconfigurator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class JsonConfigParser implements ConfigParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConfigType getSupportedType() {
        return ConfigType.JSON;
    }

    @Override
    public ConfigNode parse(Path filePath) throws Exception {
        String content = Files.readString(filePath);
        JsonNode jsonNode = objectMapper.readTree(content);
        return convertJsonNode("root", jsonNode);
    }

    private ConfigNode convertJsonNode(String name, JsonNode node) {
        if (node.isObject()) {
            List<ConfigNode> children = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                children.add(convertJsonNode(field.getKey(), field.getValue()));
            }
            return ConfigNode.branch(name, children);
        } else if (node.isArray()) {
            List<ConfigNode> children = new ArrayList<>();
            for (int i = 0; i < node.size(); i++) {
                children.add(convertJsonNode("[" + i + "]", node.get(i)));
            }
            return ConfigNode.branch(name, children);
        } else if (node.isNull()) {
            return ConfigNode.leaf(name, null);
        } else if (node.isBoolean()) {
            return ConfigNode.leaf(name, node.asBoolean());
        } else if (node.isNumber()) {
            return ConfigNode.leaf(name, node.asText());
        } else {
            return ConfigNode.leaf(name, node.asText());
        }
    }
}
