package org.xml.core;

import android.util.Log;
import android.util.Xml;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;
import org.xml.annotation.XmlValue;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmlWriter extends IXml {
    public void toXml(Object object, OutputStream outputStream) throws IOException, IllegalAccessException {
        toXml(object, outputStream, "UTF-8");
    }

    public void toXml(Object object, OutputStream outputStream, String encoding) throws IOException, IllegalAccessException {
        if (outputStream == null) return;
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outputStream, encoding);
        serializer.startDocument(encoding, null);
        writeTag(object, getTag(object.getClass()), serializer);
        serializer.endDocument();
    }

    @SuppressWarnings("unchecked")
    private void writeTag(Object object, String mainTag, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        if (mainTag == null) {
            //value
            mainTag = cls.getSimpleName();
        }
        serializer.startTag(null, mainTag);
        if (Reflect.isNormal(cls)) {
            serializer.text(toString(object));
        } else {
            writeAttributes(object, serializer);
            writeText(object, serializer);
            //
            Field[] fields = Reflect.getFileds(cls);
            Log.v("xml", cls + ":" + mainTag + " fileds=" + fields.length);
            for (Field field : fields) {
                if (XmlUtil.isXmlAttribute(field))
                    continue;
                if (XmlUtil.isXmlValue(field))
                    continue;
                XmlTag xmlTag = field.getAnnotation(XmlTag.class);
                String subTag;
                if (xmlTag == null) {
                    subTag = field.getName();
                } else {
                    subTag = xmlTag.value();
                }
                Reflect.accessible(field);
                Object value = field.get(object);
                //自定义类
                Class<?> fieldCls = field.getType();
                Log.v("xml", field.getType() + ":" + field.getName());
                if (Reflect.isNormal(fieldCls)) {
                    Log.v("xml", mainTag + " normal sub tag " + field.getName());
                    serializer.startTag(null, subTag);
                    serializer.text(toString(value));
                    serializer.endTag(null, subTag);
                } else {
                    Log.v("xml", mainTag + " other sub tag " + field.getName());
                    if (fieldCls.isArray()) {
                        //数组
                        int count = Array.getLength(value);
                        for (int i = 0; i < count; i++) {
                            writeTag(Array.get(value, i), subTag, serializer);
                        }
                    } else if (value instanceof Map) {
                        Object set = Reflect.call(value, "entrySet");
                        if (set instanceof Set) {
                            Set<Map.Entry<?, ?>> sets = (Set<Map.Entry<?, ?>>) set;
                            for (Map.Entry<?, ?> e : sets) {
                                serializer.startTag(null, subTag);
                                Log.v("xml", "map " + e);
                                writeTag(e.getKey(), MAP_KEY, serializer);
                                writeTag(e.getValue(), MAP_VALUE, serializer);
                                serializer.endTag(null, subTag);
                            }
                        }
                    } else if (value instanceof List) {
                        int count = (int) Reflect.call(value, "size");
                        for (int i = 0; i < count; i++) {
                            writeTag(Reflect.call(value, "get", i), subTag, serializer);
                        }
                    } else {
                        writeTag(value, subTag, serializer);
                    }
                }
            }
        }
        serializer.endTag(null, mainTag);
    }

    private void writeAttributes(Object object, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if (XmlUtil.isXmlTag(field))
                continue;
            if (XmlUtil.isXmlValue(field))
                continue;
            XmlAttribute xmlAttr = field.getAnnotation(XmlAttribute.class);
            String subTag;
            if (xmlAttr == null) {
                subTag = field.getName();
            } else {
                subTag = xmlAttr.value();
            }
            Reflect.accessible(field);
            Object val = field.get(object);
            Log.v("xml", subTag + "=" + val);
            serializer.attribute(null, subTag, toString(val));
        }
    }

    private void writeText(Object object, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if (XmlUtil.isXmlAttribute(field))
                continue;
            if (XmlUtil.isXmlTag(field))
                continue;
            XmlValue xmlValue = field.getAnnotation(XmlValue.class);
            Reflect.accessible(field);
            Object val = field.get(object);
            if (xmlValue != null) {
                serializer.text(toString(val));
            }
        }
    }
}
