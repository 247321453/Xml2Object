package org.xml.core;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Element {
    public Element() {
        super();
        attributes = new HashMap<>();
        mElements = new ArrayList<>();
    }

    public Element(String name) {
        this();
        this.name = name;
    }

    private String text;

    private String name;

    private Class<?> tClass;

    private final Map<String, String> attributes;
    private final List<Element> mElements;

    private final List<String> xmlnames = new ArrayList<>();

    public List<Element> getElements() {
        return mElements;
    }

    public int size() {
        return mElements.size();
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void addAttribute(String name, String value) {
        if (name == null) return;
        if (!xmlnames.contains(name)) {
            xmlnames.add(name);
        }
        attributes.put(name, value);
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public void add(Element element) {
        if (element != null) {
            if (!xmlnames.contains(element.getName())) {
                xmlnames.add(element.getName());
            }
            mElements.add(element);
        }
    }

    public List<String> getTagNames() {
        if (IXml.DEBUG)
            Log.d("xml", "size=" + mElements.size());
        return xmlnames;
    }

    public Element get(int i) {
        if (i >= 0 && i < size()) {
            return mElements.get(i);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        if (text == null) return "";
        return text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String value) {
        this.text = value;
    }

    public boolean isArray() {
        if (tClass == null) return false;
        return tClass.isArray();
    }

    public boolean isList() {
        if (tClass == null) return false;
        return Collection.class.isAssignableFrom(tClass);
    }

    public boolean isMap() {
        if (tClass == null) return false;
        return Map.class.isAssignableFrom(tClass);
    }

    public void addAll(Collection<Element> collection) {
        if (collection != null) {
            for (Element e : collection) {
                add(e);
            }
        }
    }

    public Element get(String name) {
        if (name == null) return null;
        for (Element t : mElements) {
            if (name.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Element> getElementList(String name) {
        ArrayList<Element> elements = new ArrayList<>();
        if (name == null) {
            if (IXml.DEBUG)
                Log.w("xml", "name is null");
            return elements;
        }
        for (Element t : this.mElements) {
            if (name.equals(t.getName())) {
                elements.add(t);
            }
        }
        return elements;
    }

    public Class<?> getTClass() {
        return tClass;
    }

    public void setClass(Class<?> tClass) {
        this.tClass = tClass;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", attributes=" + attributes +
                ", tags=" + mElements +
                '}';
    }
}
