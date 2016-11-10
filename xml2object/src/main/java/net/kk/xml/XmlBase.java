package net.kk.xml;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElement;
import net.kk.xml.annotations.XmlElementList;
import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.annotations.XmlElementText;
import net.kk.xml.annotations.XmlIgnore;
import net.kk.xml.internal.Reflect;
import net.kk.xml.internal.XmlConstructorAdapter;
import net.kk.xml.internal.XmlConstructors;
import net.kk.xml.internal.XmlStringAdapter;
import net.kk.xml.internal.XmlStringAdapters;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XmlBase {
    public static final String DEF_ENCODING = "UTF-8";
    final Map<Class<?>, XmlStringAdapter<?>> mXmlTypeAdapterMap;
    final Map<Class<?>, XmlConstructorAdapter> mXmlConstructorAdapterMap;
    private XmlStringAdapters mXmlStringAdapters;
    private XmlConstructors xmlConstructors;
    protected XmlOptions mOptions;
    private final XmlStringAdapter<?> DEFAULT_ADAPTER;
    private final XmlConstructorAdapter DEFAULT_CONSTRUCTOR;
    public final boolean DEBUG;

    public XmlBase(XmlOptions options) {
        this.mOptions = (options == null) ? XmlOptions.DEFAULT : options;
        this.DEBUG = this.mOptions.isDebug();
        mXmlStringAdapters = new XmlStringAdapters();
        xmlConstructors = new XmlConstructors();
        mXmlConstructorAdapterMap = new HashMap<Class<?>, XmlConstructorAdapter>();
        mXmlTypeAdapterMap = new HashMap<Class<?>, XmlStringAdapter<?>>();
        DEFAULT_ADAPTER = mXmlStringAdapters.ObjectStringAdapter;
        DEFAULT_CONSTRUCTOR = xmlConstructors.objectXmlConstructorAdapter;
        //TODO register mXmlStringAdapters
        mXmlTypeAdapterMap.put(Object.class, DEFAULT_ADAPTER);
        mXmlConstructorAdapterMap.put(Object.class, DEFAULT_CONSTRUCTOR);
        if (this.mOptions.getXmlTypeAdapterMap() != null) {
            mXmlTypeAdapterMap.putAll(this.mOptions.getXmlTypeAdapterMap());
        }
        if(this.mOptions.getXmlConstructorAdapterMap()!=null){
            mXmlConstructorAdapterMap.putAll(this.mOptions.getXmlConstructorAdapterMap());
        }
    }

    public XmlBase() {
        this(XmlOptions.DEFAULT);
    }

    public XmlStringAdapter getAdapter(Class<?> tClass) {
        return getAdapter(tClass, DEFAULT_ADAPTER);
    }

    public XmlConstructorAdapter getConstructor(Class<?> tClass) {
        return getConstructor(tClass, DEFAULT_CONSTRUCTOR);
    }

    public XmlConstructorAdapter getConstructor(Class<?> tClass, XmlConstructorAdapter def) {
        if (mXmlTypeAdapterMap == null) return def;
        Class<?> key = Reflect.wrapper(tClass);
        XmlConstructorAdapter tXmlTypeAdapter = mXmlConstructorAdapterMap.get(key);
        if (tXmlTypeAdapter == null) return def;//;
        return tXmlTypeAdapter;
    }

    public XmlStringAdapter<?> getAdapter(Class<?> tClass, XmlStringAdapter<?> def) {
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
        if(cls.getAnnotation(XmlIgnore.class)!=null){
            return null;
        }
        if (cls instanceof Field) {
            Field field = (Field) cls;
            if ((field.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT) {
                return null;
            }
        }
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
                return field.getName();
            } else if (cls instanceof Class<?>) {
                return ((Class<?>) cls).getSimpleName();
            }
        }
        return null;
    }

    public String getNamespace(AnnotatedElement element) {
        String namespace = null;
        if (element == null) return null;
        XmlAttribute attribute = element.getAnnotation(XmlAttribute.class);
        if (attribute != null) {
            namespace = attribute.namespace();
        }
        XmlElement xmlElement = element.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            namespace = xmlElement.namespace();
        }
        XmlElementList xmlElementList = element.getAnnotation(XmlElementList.class);
        if (xmlElementList != null) {
            namespace = xmlElementList.namespace();
        }
        XmlElementMap xmlElementMap = element.getAnnotation(XmlElementMap.class);
        if (xmlElementMap != null) {
            namespace = xmlElementMap.namespace();
        }
        if (namespace != null && namespace.trim().length() == 0) {
            return null;
        }
        return namespace;
    }

    protected Class<?> getListClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
        if (cls instanceof Field) {
            return Reflect.getListClass((Field) cls);
        }
        XmlElementList xmlElement = cls.getAnnotation(XmlElementList.class);
        if (xmlElement != null) {
            if (xmlElement.type() != null) {
                return xmlElement.type();
            }
        }
        return Object.class;
    }

    protected Class<?>[] getMapClass(AnnotatedElement cls) {
        if (cls == null)
            return new Class[]{Object.class, Object.class};
        if (cls instanceof Field) {
            return Reflect.getMapClass((Field) cls);
        }
        XmlElementMap xmlElement = cls.getAnnotation(XmlElementMap.class);
        Class<?> kclass = Object.class;
        Class<?> vclass = Object.class;
        if (xmlElement != null) {
            if (xmlElement.keyType() != null) {
                kclass = xmlElement.keyType();
            }
            if (xmlElement.valueType() != null) {
                vclass = xmlElement.valueType();
            }
        }
        return new Class[]{kclass, vclass};
    }

    protected static String trim(String str) {
        if (str == null) return null;
        return str.trim();
    }
}
