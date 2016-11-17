package net.kk.xml.core;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

class XmlObject {
    public XmlObject() {
        super();
    }

    public XmlObject(String name) {
        this();
        setName(name);
    }

    // xml的文本
    private String text;

    // 标签名
    private String name;

    private Field tField;

    // 类型
    private Class<?> tClass;

    // 子元素，用来打印
    private boolean subItem;

    // 属性
    private List<XmlAttributeObject> attributes;
    // 子标签
    private List<XmlObject> mXmlObjects;

    private String namespace;

    private int index = Integer.MAX_VALUE;

    /***
     * xml属性集合
     */
    public List<XmlAttributeObject> getAttributes() {
        return attributes;
    }

    /***
     * 命名空间
     *
     * @return
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * list，map的子标签
     */
    public boolean isSubItem() {
        return subItem;
    }

    /**
     * list，map的子标签
     */
    public void setSubItem(boolean subItem) {
        this.subItem = subItem;
    }

    /***
     * 添加属性
     *
     * @param namespace 命名空间
     * @param name      名字
     * @param value     值
     */
    public void addAttribute(String namespace, String name, String value) {
        if (name == null)
            return;
        if (attributes == null) {
            attributes = new ArrayList<XmlAttributeObject>();
        }
        XmlAttributeObject xmlAttributeObject = new XmlAttributeObject(namespace, name, value);
        int index = attributes.indexOf(xmlAttributeObject);
        if (index >= 0) {
            attributes.remove(index);
        }
        attributes.add(xmlAttributeObject);
    }

    /**
     * 空
     */
    public boolean isNULL() {
        return tClass == null;
    }

    /***
     * 添加字标签
     *
     * @param xmlObject
     */
    public void addChild(XmlObject xmlObject) {
        if (mXmlObjects == null) {
            mXmlObjects = new ArrayList<XmlObject>();
        }
        if (xmlObject != null) {
            mXmlObjects.add(xmlObject);
        }
    }

    /***
     * xml标签数量
     *
     * @return
     */
    public int getChildCount() {
        return mXmlObjects == null ? 0 : mXmlObjects.size();
    }

    /***
     * 获取字标签
     *
     * @param i
     * @return
     */
    public XmlObject getChildAt(int i) {
        if (mXmlObjects == null)
            return null;
        if (i >= 0 && i < getChildCount()) {
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

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String value) {
        this.text = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void addAllChilds(Collection<XmlObject> collection) {
        if (collection != null) {
            for (XmlObject e : collection) {
                addChild(e);
            }
        }
    }

    public List<XmlObject> getAllChilds() {
        return mXmlObjects;
    }

    HashMap<String, ArrayList<XmlObject>> mCache;

    private void makeCache() {
        if (mCache == null) {
            synchronized (this) {
                if (mCache == null) {
                    mCache = new HashMap<>();
                }
            }
            for (XmlObject t : mXmlObjects) {
                String name = t.getName();
                ArrayList<XmlObject> list = mCache.get(name);
                if (list == null) {
                    list = new ArrayList<>();
                    mCache.put(name, list);
                }
                list.add(t);
            }
        }
    }

    public XmlObject getChild(String name) {
        if (name == null || mXmlObjects == null)
            return null;
        makeCache();
        ArrayList<XmlObject> items=getSameChild(name);
        if(items!=null&&items.size()>0){
            return items.get(0);
        }
        return null;
    }

    public ArrayList<XmlObject> getSameChild(String name) {
        if (name == null || this.mXmlObjects == null) {
            return null;
        }
        makeCache();
        return mCache.get(name);
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

    private Field getTField() {
        return tField;
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
