package org.xml.core;

import com.uutils.xml2object.BuildConfig;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlIgnore;
import org.xml.annotation.XmlElement;
import org.xml.annotation.XmlElementText;

import java.lang.reflect.AnnotatedElement;

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

    protected String getTagName(AnnotatedElement cls, String def) {
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            //text
            return xmlElement.value();
        }
        return def;
    }
}
