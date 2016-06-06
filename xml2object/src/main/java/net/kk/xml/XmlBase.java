package net.kk.xml;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElement;
import net.kk.xml.annotations.XmlElementList;
import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.annotations.XmlElementText;
import net.kk.xml.internal.XmlOptions;
import net.kk.xml.internal.XmlTypeAdapter;
import net.kk.xml.internal.bind.ObjectTypeAdapter;
import net.kk.xml.internal.bind.Reflect;
import net.kk.xml.internal.bind.XmlTypeAdapters;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

class XmlBase {
    public static final String DEF_ENCODING = "UTF-8";
    final Map<String, XmlTypeAdapter<?>> mXmlTypeAdapterMap;
    private XmlTypeAdapters mXmlTypeAdapters;
    protected XmlOptions mOptions;
    private final XmlTypeAdapter<?> DEFAULT_ADAPTER;
    public final boolean DEBUG;

    public XmlBase(XmlOptions options) {
        this.mOptions = (options == null) ? XmlOptions.DEFAULT : options;
        DEBUG = this.mOptions.isDebug();
        DEFAULT_ADAPTER = new ObjectTypeAdapter();
        mXmlTypeAdapterMap = this.mOptions.getXmlTypeAdapterMap();
        mXmlTypeAdapters = new XmlTypeAdapters();
        //TODO register mXmlTypeAdapters
        if (mXmlTypeAdapterMap != null) {

        }
    }

    public XmlBase() {
        this(XmlOptions.DEFAULT);
    }

    public XmlTypeAdapter<?> getAdapter(Class<?> tClass) {
        return getAdapter(tClass, DEFAULT_ADAPTER);
    }

    public XmlTypeAdapter<?> getAdapter(Class<?> tClass, XmlTypeAdapter def) {
        if (mXmlTypeAdapterMap == null) return def;
        String key = Reflect.wrapper(tClass).getName();
        XmlTypeAdapter<?> tXmlTypeAdapter = mXmlTypeAdapterMap.get(key);
        if (tXmlTypeAdapter == null) return def;//;
        return tXmlTypeAdapter;
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
                return ((Field) cls).getName();
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
