package org.xml.core;

import android.util.Log;

import com.uutils.xml2object.BuildConfig;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlIgnore;
import org.xml.annotation.XmlElement;
import org.xml.annotation.XmlElementText;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

abstract class IXml {
    public static final String MAP_KEY = "key";
    public static final String MAP_VALUE = "text";
    public static final String DEF_ENCODING = "UTF-8";

    public final static boolean DEBUG = BuildConfig.DEBUG;

    protected boolean isXmlAttribute(AnnotatedElement field) {
        XmlAttribute xmlAttr = field.getAnnotation(XmlAttribute.class);
        return xmlAttr != null;
    }

    protected boolean isXmlTag(AnnotatedElement field) {
        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
        return xmlElement != null;
    }

    protected boolean isXmlValue(AnnotatedElement field) {
        XmlElementText xmlElementText = field.getAnnotation(XmlElementText.class);
        return xmlElementText != null;
    }

    protected boolean isXmlIgnore(AnnotatedElement field) {
        XmlIgnore value = field.getAnnotation(XmlIgnore.class);
        return value != null;
    }

    protected String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    protected String getAttributeName(AnnotatedElement cls, String def) {
        XmlAttribute value = cls.getAnnotation(XmlAttribute.class);
        if (value != null) {
            return value.value();
        }
        return def;
    }

    protected Class<?> getArrayClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            if (xmlElement.type() != null) {
                return xmlElement.type();
            }
        } else {
            if (cls instanceof Class) {
                return ((Class<?>) cls).getComponentType();
            } else if (cls instanceof Field) {
                return ((Field) cls).getType().getComponentType();
            } else {
                if (IXml.DEBUG)
                    Log.w("xml", cls + " not's xmltag");
            }
        }
        return Object.class;
    }

    protected Class<?> getListClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            if (xmlElement.type() != null) {
                return xmlElement.type();
            }
        } else {
            if (IXml.DEBUG)
                Log.w("xml", cls + " not's xmltag");
        }
        return Object.class;
    }

    protected Class<?>[] getMapClass(AnnotatedElement cls) {
        if (cls == null)
            return new Class[]{Object.class, Object.class};
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
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

    protected String getTagName(AnnotatedElement cls) {
        String name;
        if (cls instanceof Class) {
            name = ((Class<?>) cls).getSimpleName();
        } else {
            name = ((Field) cls).getName();
        }
        return getTagName(cls, name);
    }

    protected String getTagName(AnnotatedElement cls, String def) {
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            //text
            return xmlElement.value();
        }
        return def;
    }
}
