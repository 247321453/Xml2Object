package net.kk.xml;


import net.kk.xml.internal.Reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XmlObject {
    public XmlObject() {
        super();
    }

    public XmlObject(String name) {
        this();
        this.name = name;
    }

    //xml的文本
    private String text;

    //标签名
    private String name;

    private Field tField;

    //类型
    private Class<?> tClass;

    //子元素，用来打印
    private boolean subItem;

    //属性
    private Map<String, String> attributes;
    //子标签
    private List<XmlObject> mXmlObjects;

    //子元素数量
    public int size() {
        return mXmlObjects == null ? 0 : mXmlObjects.size();
    }

    //属性值
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public boolean isSubItem() {
        return subItem;
    }

    public void setSubItem(boolean subItem) {
        this.subItem = subItem;
    }

    public void addAttribute(String name, String value) {
        if (name == null) return;
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        attributes.put(name, value);
    }

    public boolean isNULL() {
        return tClass == null;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public void add(XmlObject xmlObject) {
        if (mXmlObjects == null) {
            mXmlObjects = new ArrayList<XmlObject>();
        }
        if (xmlObject != null) {
            mXmlObjects.add(xmlObject);
        }
    }

    public XmlObject get(int i) {
        if (mXmlObjects == null) return null;
        if (i >= 0 && i < size()) {
            return mXmlObjects.get(i);
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

    public void addAll(Collection<XmlObject> collection) {
        if (collection != null) {
            for (XmlObject e : collection) {
                add(e);
            }
        }
    }

    public XmlObject get(String name) {
        if (name == null || mXmlObjects == null) return null;
        for (XmlObject t : mXmlObjects) {
            if (name.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    public XmlObject getElement(String name) {
        if (name == null || mXmlObjects == null) {
            return null;
        }
        for (XmlObject t : this.mXmlObjects) {
            if (name.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<XmlObject> getElementList(String name) {
        ArrayList<XmlObject> xmlObjects = new ArrayList<XmlObject>();
        if (name == null || this.mXmlObjects == null) {
            return xmlObjects;
        }
        for (XmlObject t : this.mXmlObjects) {
            if (name.equals(t.getName())) {
                xmlObjects.add(t);
            }
        }
        return xmlObjects;
    }

    public Class<?> getTClass() {
        if (tClass == null) {
            if (tField == null) {
                return null;
            }
            return tField.getType();
        }
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
        try {
            if (Reflect.isNormal(tClass)) {
                stringBuffer.append(" = " + text + "\n");
                return stringBuffer.toString();
            }
        } catch (Exception e) {

        }
        stringBuffer.append("{text='" + (text == null ? "" : text));
        stringBuffer.append("', attributes=" + attributes);
        stringBuffer.append(", tags=");
        if (mXmlObjects == null || mXmlObjects.size() == 0) {
            stringBuffer.append("null}\n");
        } else {
            stringBuffer.append("[\n");
            for (XmlObject xmlObject : mXmlObjects) {
                for (int i = 0; i < start + 1; i++) {
                    stringBuffer.append("\t");
                }
                stringBuffer.append(xmlObject.toString(start + 1));
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
