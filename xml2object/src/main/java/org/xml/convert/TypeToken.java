package org.xml.convert;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeToken {
    public TypeToken() {
        super();
        attributes = new HashMap<>();
        mTypeTokens = new ArrayList<>();
    }

    public TypeToken(String name) {
        this();
        this.name = name;
    }

    private String text;

    private String name;

    private Field tField;

    private Class<?> tClass;

    private final Map<String, String> attributes;
    private final List<TypeToken> mTypeTokens;

    private final List<String> xmlnames = new ArrayList<>();

    public List<TypeToken> getTypeTokens() {
        return mTypeTokens;
    }

    public int size() {
        return mTypeTokens.size();
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

    public void add(TypeToken typeToken) {
        if (typeToken != null) {
            if (!xmlnames.contains(typeToken.getName())) {
                xmlnames.add(typeToken.getName());
            }
            mTypeTokens.add(typeToken);
        }
    }

    public void updateTClass(Class<?> pTClass) {
        tClass = pTClass;
    }

    public List<String> getTagNames() {
        return xmlnames;
    }

    public TypeToken get(int i) {
        if (i >= 0 && i < size()) {
            return mTypeTokens.get(i);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getText() {
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

    public void addAll(Collection<TypeToken> collection) {
        if (collection != null) {
            for (TypeToken e : collection) {
                add(e);
            }
        }
    }

    public TypeToken get(String name) {
        if (name == null) return null;
        for (TypeToken t : mTypeTokens) {
            if (name.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }
//
//    public ArrayList<TypeToken> getElementList(String name) {
//        ArrayList<TypeToken> typeTokens = new ArrayList<>();
//        if (name == null) {
//            if (KXml.DEBUG)
//                Log.w("xml", "name is null");
//            return typeTokens;
//        }
//        for (TypeToken t : this.mTypeTokens) {
//            if (name.equals(t.getName())) {
//                typeTokens.add(t);
//            }
//        }
//        return typeTokens;
//    }

    public Class<?> getTClass() {
        return tClass;
    }

    public AnnotatedElement getType() {
        return this.tField == null ? this.tClass : this.tField;
    }

    public void setType(AnnotatedElement pType) {
        if (pType instanceof Field) {
            this.tField = (Field) pType;
            this.tClass = this.tField.getType();
        } else {
            this.tField = null;
            this.tClass = (Class<?>) pType;
        }
    }

    public String toString(int start) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(name + ":");
        stringBuffer.append("" + (tClass == null ? "" : tClass.getName()));
        stringBuffer.append("{text='" + (text == null ? "" : text));
        stringBuffer.append("', attributes=" + attributes);
        stringBuffer.append(", tags=");
        if (mTypeTokens.size() == 0) {
            stringBuffer.append("[]}\n");
        } else {
            stringBuffer.append("[\n");
            for (TypeToken typeToken : mTypeTokens) {
                for (int i = 0; i < start + 1; i++) {
                    stringBuffer.append("\t");
                }
                stringBuffer.append(typeToken.toString(start + 1));
            }
            for (int i = 0; i < start; i++) {
                stringBuffer.append("\t");
            }
            stringBuffer.append("]\n");
        }
        return stringBuffer.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
