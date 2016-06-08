package net.kk.xml;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElement;
import net.kk.xml.annotations.XmlElementList;
import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.annotations.XmlElementText;
import net.kk.xml.internal.Reflect;
import net.kk.xml.internal.XmlStringAdapter;
import net.kk.xml.internal.XmlTypeAdapters;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XmlBase {
    public static final String DEF_ENCODING = "UTF-8";
    final Map<Class<?>, XmlStringAdapter<?>> mXmlTypeAdapterMap;
    private XmlTypeAdapters mXmlTypeAdapters;
    protected XmlOptions mOptions;
    private final XmlStringAdapter<?> DEFAULT_ADAPTER;
    public final boolean DEBUG;

    public XmlBase(XmlOptions options) {
        this.mOptions = (options == null) ? XmlOptions.DEFAULT : options;
        this.DEBUG = this.mOptions.isDebug();
        mXmlTypeAdapters = new XmlTypeAdapters();
        mXmlTypeAdapterMap = new HashMap<Class<?>, XmlStringAdapter<?>>();
        DEFAULT_ADAPTER = mXmlTypeAdapters.ObjectStringAdapter;

        //TODO register mXmlTypeAdapters
        mXmlTypeAdapterMap.put(Object.class, mXmlTypeAdapters.ObjectStringAdapter);

        if (this.mOptions.getXmlTypeAdapterMap() != null) {
            mXmlTypeAdapterMap.putAll(this.mOptions.getXmlTypeAdapterMap());
        }
    }

    public XmlBase() {
        this(XmlOptions.DEFAULT);
    }

    public XmlStringAdapter<?> getAdapter(Class<?> tClass) {
        return getAdapter(tClass, DEFAULT_ADAPTER);
    }

    public XmlStringAdapter<?> getAdapter(Class<?> tClass, XmlStringAdapter def) {
        if (mXmlTypeAdapterMap == null) return def;
        Class<?> key = Reflect.wrapper(tClass);
        XmlStringAdapter<?> tXmlTypeAdapter = mXmlTypeAdapterMap.get(key);
        if (tXmlTypeAdapter == null) return def;//;
        return tXmlTypeAdapter;
    }

    public boolean isNormal(AnnotatedElement element) throws IllegalAccessException {
        XmlElement element1 = element.getAnnotation(XmlElement.class);
        if (element1 != null && element1.isString()) {
            return true;
        }
        if (element instanceof Field) {
            Field field = (Field) element;
            return Reflect.isNormal(field.getType());
        } else if (element instanceof Class) {
            Class<?> cls = (Class<?>) element;
            return Reflect.isNormal(cls);
        }
        return false;
    }
    public XmlOptions getOptions() {
        return mOptions;
    }

    public String getTagName(AnnotatedElement cls) {
        if (cls == null) return null;
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            //text
            return trim(xmlElement.value());
        }
        XmlElementList xmlElementList = cls.getAnnotation(XmlElementList.class);
        if (xmlElementList != null) {
            //text
            return trim(xmlElementList.value());
        }
        XmlElementMap xmlElementMap = cls.getAnnotation(XmlElementMap.class);
        if (xmlElementMap != null) {
            //text
            return trim(xmlElementMap.value());
        }
        //是否返回变量名？
        return getName(cls);
    }

    public String getItemTagName(AnnotatedElement cls) {
        XmlElementList xmlElementList = cls.getAnnotation(XmlElementList.class);
        if (xmlElementList != null) {
            //text
            return trim(xmlElementList.item());
        }
        XmlElementMap xmlElementMap = cls.getAnnotation(XmlElementMap.class);
        if (xmlElementMap != null) {
            //text
            return trim(xmlElementMap.item());
        }
        return XmlElementList.ITEM;
    }

    public boolean isXmlElementText(AnnotatedElement field) {
        XmlElementText xmlElementText = field.getAnnotation(XmlElementText.class);
        return xmlElementText != null;
    }

    public String getAttributeName(AnnotatedElement cls) {
        XmlAttribute value = cls.getAnnotation(XmlAttribute.class);
        if (value != null) {
            return value.value();
        }
        return null;
    }

    public boolean isArray(Class<?> cls) {
        if (cls == null) return false;
        return List.class.isAssignableFrom(cls) || cls.isArray() || Map.class.isAssignableFrom(cls);
    }

    private String getName(AnnotatedElement cls) {
        if (mOptions.isUseNoAnnotation()) {
            //没有注解也注解
            if (cls instanceof Field) {
                Field field = (Field) cls;
                if ((field.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT) {
                    return null;
                }
                return field.getName();
            } else if (cls instanceof Class<?>) {
                return ((Class) cls).getName();
            }
        }
        return null;
    }

    protected static String trim(String str) {
        if (str == null) return null;
        return str.trim();
    }
}
