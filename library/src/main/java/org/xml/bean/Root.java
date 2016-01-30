package org.xml.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Root {
    public Root() {
        tags = new ArrayList<>();
    }

    protected final List<Tag> tags;

    public int size() {
        return tags.size();
    }

    public void add(Tag tag) {
        if (tag != null)
            tags.add(tag);
    }

    public void addAll(Collection<Tag> collection) {
        if (collection != null)
            tags.addAll(collection);
    }

    public Tag get(String name) {
        if (name == null) return null;
        for (Tag t : tags) {
            if (name.equals(t.name)) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Tag> getList(String name) {
        ArrayList<Tag> tags = new ArrayList<>();
        if (name == null) return tags;
        for (Tag t : tags) {
            if (name.equals(t.name)) {
                tags.add(t);
            }
        }
        return tags;
    }
}
