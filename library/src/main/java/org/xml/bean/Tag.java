package org.xml.bean;

import java.util.HashMap;
import java.util.Map;

public class Tag extends Root {
    public Tag() {
        super();
        attributes = new HashMap<>();
    }

    public Tag(String name) {
        this();
        this.name = name;
    }

    public String value;

    public String name;
    public final Map<String, String> attributes;
}
