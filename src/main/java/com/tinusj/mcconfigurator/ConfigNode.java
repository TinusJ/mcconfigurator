package com.tinusj.mcconfigurator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigNode {
    private final String name;
    private final Object value;
    private final boolean isLeaf;
    private final List<ConfigNode> children;

    private ConfigNode(String name, Object value, boolean isLeaf, List<ConfigNode> children) {
        this.name = name;
        this.value = value;
        this.isLeaf = isLeaf;
        this.children = children != null ? new ArrayList<>(children) : new ArrayList<>();
    }

    public static ConfigNode leaf(String name, Object value) {
        return new ConfigNode(name, value, true, null);
    }

    public static ConfigNode branch(String name, List<ConfigNode> children) {
        return new ConfigNode(name, null, false, children);
    }

    public static ConfigNode branch(String name) {
        return new ConfigNode(name, null, false, new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public List<ConfigNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(ConfigNode child) {
        if (!isLeaf) {
            children.add(child);
        }
    }
}
