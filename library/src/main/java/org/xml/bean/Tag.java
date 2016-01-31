package org.xml.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag {
    public Tag() {
        super();
        attributes = new HashMap<>();
        tags = new ArrayList<>();
    }

    public Tag(String name) {
        this();
        this.name = name;
    }

    protected boolean isArray = false;
    protected boolean isMap = false;
    protected String value;

    protected String name;

    public final Map<String, String> attributes;
    protected final List<Tag> tags;

    public int size() {
        return tags.size();
    }

    public void add(Tag tag) {
        if (tag != null)
            tags.add(tag);
    }

    public Tag get(int i) {
        if (i >= 0 && i < size()) {
            return tags.get(i);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        if (value == null) return "";
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isMap() {
        return isMap;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }

    public void addAll(Collection<Tag> collection) {
        if (collection != null)
            tags.addAll(collection);
    }

    public Tag get(String name) {
        if (name == null) return null;
        for (Tag t : tags) {
            if (name.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Tag> getList(String name) {
        ArrayList<Tag> tags = new ArrayList<>();
        if (name == null) return tags;
        for (Tag t : tags) {
            if (name.equals(t.getName())) {
                tags.add(t);
            }
        }
        return tags;
    }

    public void setIsMap(boolean isMap) {
        this.isMap = isMap;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "attributes=" + attributes +
                ", isArray=" + isArray +
                ", isMap=" + isMap +
                ", value='" + value + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                '}';
    }
}
