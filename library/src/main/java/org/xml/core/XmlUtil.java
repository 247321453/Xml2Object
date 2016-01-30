package org.xml.core;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;
import org.xml.annotation.XmlValue;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/1/30.
 */
class XmlUtil {
    public static boolean isXmlAttribute(Field field) {
        XmlAttribute xmlAttr = field.getAnnotation(XmlAttribute.class);
        return xmlAttr != null;
    }

    public static boolean isXmlTag(Field field) {
        XmlTag xmlTag = field.getAnnotation(XmlTag.class);
        return xmlTag != null;
    }

    public static boolean isXmlValue(Field field) {
        XmlValue xmlValue = field.getAnnotation(XmlValue.class);
        return xmlValue != null;
    }
}
