package org.xml.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag {
    public Tag() {
        attributes = new HashMap<>();
        tags = new ArrayList<>();
    }

    public String name;
    public String value;
    public final Map<String, String> attributes;
    public final List<Tag> tags;

    public Tag get(String name) {
        if (name == null) return null;
        for (Tag t : tags) {
            if (name.equals(t.name)) {
                return t;
            }
        }
        return null;
    }
}
