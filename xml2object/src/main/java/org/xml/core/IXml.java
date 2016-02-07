package org.xml.core;

import android.util.Log;

import com.uutils.xml2object.BuildConfig;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlElementArray;
import org.xml.annotation.XmlElementMap;
import org.xml.annotation.XmlElement;
import org.xml.annotation.XmlElementText;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

abstract class IXml {
    public static final String MAP_KEY = "key";
    public static final String MAP_VALUE = "value";
    public static final String DEF_ENCODING = "UTF-8";

    public final static boolean DEBUG = BuildConfig.DEBUG;

    protected boolean isXmlAttribute(AnnotatedElement field) {
        XmlAttribute xmlAttr = field.getAnnotation(XmlAttribute.class);
        return xmlAttr != null;
    }

    protected boolean isXmlValue(AnnotatedElement field) {
        XmlElementText xmlElementText = field.getAnnotation(XmlElementText.class);
        return xmlElementText != null;
    }

    protected String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    public static String getAttributeName(AnnotatedElement cls) {
        XmlAttribute value = cls.getAnnotation(XmlAttribute.class);
        if (value != null) {
            return value.value();
        }
        return null;
    }

    protected Class<?> getArrayClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
        XmlElementArray xmlElement = cls.getAnnotation(XmlElementArray.class);
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
        XmlElementArray xmlElement = cls.getAnnotation(XmlElementArray.class);
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

    public static String getTagName(AnnotatedElement cls) {
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            //text
            return xmlElement.value();
        }
        XmlElementArray xmlElementArray = cls.getAnnotation(XmlElementArray.class);
        if (xmlElementArray != null) {
            //text
            return xmlElementArray.value();
        }
        XmlElementMap xmlElementMap = cls.getAnnotation(XmlElementMap.class);
        if (xmlElementMap != null) {
            //text
            return xmlElementMap.value();
        }
        return null;
    }
}
