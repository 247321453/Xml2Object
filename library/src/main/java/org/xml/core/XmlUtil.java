package org.xml.core;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlIgnore;
import org.xml.annotation.XmlMap;
import org.xml.annotation.XmlTag;
import org.xml.annotation.XmlValue;

import java.lang.reflect.AnnotatedElement;

class XmlUtil {
    public static boolean isXmlAttribute(AnnotatedElement field) {
        XmlAttribute xmlAttr = field.getAnnotation(XmlAttribute.class);
        return xmlAttr != null;
    }

    public static boolean isXmlTag(AnnotatedElement field) {
        XmlTag xmlTag = field.getAnnotation(XmlTag.class);
        return xmlTag != null;
    }

    public static boolean isXmlValue(AnnotatedElement field) {
        XmlValue xmlValue = field.getAnnotation(XmlValue.class);
        return xmlValue != null;
    }
    public static boolean isXmlIgnore(AnnotatedElement field) {
        XmlIgnore value = field.getAnnotation(XmlIgnore.class);
        return value != null;
    }
    public static boolean isXmlMap(AnnotatedElement field) {
        XmlMap value = field.getAnnotation(XmlMap.class);
        return value != null;
    }
}
