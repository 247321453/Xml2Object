package org.xml.core;

import org.xml.annotation.XmlTag;

import java.lang.reflect.Field;

abstract class IXml {

    protected static final String MAP_KEY = "key";
    protected static final String MAP_VALUE = "value";

    protected static String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    protected String getTag(Field field) {
        XmlTag xmltag = field.getAnnotation(XmlTag.class);
        if (xmltag == null) {
            return field.getName();
        }
        return xmltag.value();
    }

    protected String getTag(Class<?> cls) {
        XmlTag xmlTag = cls.getAnnotation(XmlTag.class);
        if (xmlTag != null) {
            //value
            return xmlTag.value();
        } else {
            return cls.getSimpleName();
        }
    }
}
