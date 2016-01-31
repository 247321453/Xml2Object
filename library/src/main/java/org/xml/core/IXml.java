package org.xml.core;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlMap;
import org.xml.annotation.XmlTag;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

abstract class IXml {

    protected static String toString(Object obj) {
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
            //value
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
        XmlMap xmlMap = cls.getAnnotation(XmlMap.class);
        if (xmlMap != null) {
            return xmlMap.name();
        }
        return def;
    }
}
