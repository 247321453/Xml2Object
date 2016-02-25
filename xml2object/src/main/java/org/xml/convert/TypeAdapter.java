package org.xml.convert;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlElement;
import org.xml.annotation.XmlElementList;
import org.xml.annotation.XmlElementMap;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public abstract class TypeAdapter<T> {

    public abstract T read(TypeToken token, AnnotatedElement type,Object old);

    public abstract void write(TypeToken parent,AnnotatedElement type,String name, T t);

    public static Class<?> getType(AnnotatedElement element) {
        if (element instanceof Class) {
            return ((Class) element);
        } else if (element instanceof Field) {
            return ((Field) element).getType();
        }
        return Object.class;
    }

    public static String getTagName(AnnotatedElement element) {
        XmlElement xmlElement = element.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            return xmlElement.value();
        }
        XmlElementList xmlElementList = element.getAnnotation(XmlElementList.class);
        if (xmlElementList != null) {
            return xmlElementList.value();
        }
        XmlElementMap xmlElementMap = element.getAnnotation(XmlElementMap.class);
        if (xmlElementMap != null) {
            return xmlElementMap.value();
        }
        XmlAttribute xmlAttribute = element.getAnnotation(XmlAttribute.class);
        if (xmlAttribute != null) {
            return xmlAttribute.value();
        }
        if (element instanceof Class) {
            return ((Class) element).getSimpleName();
        } else if (element instanceof Field) {
            return ((Field) element).getName();
        }
        return "unknown";
    }

}
