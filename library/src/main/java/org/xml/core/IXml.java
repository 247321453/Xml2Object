package org.xml.core;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlIgnore;
import org.xml.annotation.XmlTag;
import org.xml.annotation.XmlValue;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

abstract class IXml {
    static final String MAP_KEY = "key";
    static final String MAP_VALUE = "value";

    protected boolean isXmlAttribute(AnnotatedElement field) {
        XmlAttribute xmlAttr = field.getAnnotation(XmlAttribute.class);
        return xmlAttr != null;
    }

    protected boolean isXmlTag(AnnotatedElement field) {
        XmlTag xmlTag = field.getAnnotation(XmlTag.class);
        return xmlTag != null;
    }

    protected boolean isXmlValue(AnnotatedElement field) {
        XmlValue xmlValue = field.getAnnotation(XmlValue.class);
        return xmlValue != null;
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

    protected String getTagName(Field field) {
        XmlTag xmltag = field.getAnnotation(XmlTag.class);
        if (xmltag == null) {
            return field.getName();
        }
        return xmltag.value();
    }

    protected String getAttributeName(AnnotatedElement cls, String def) {
        XmlAttribute value = cls.getAnnotation(XmlAttribute.class);
        if (value != null) {
            return value.value();
        }
        return def;
    }

    protected String getTagName(AnnotatedElement cls, String def) {
        XmlTag xmlTag = cls.getAnnotation(XmlTag.class);
        if (xmlTag != null) {
            //value
            return xmlTag.value();
        }
        return def;
    }
}
