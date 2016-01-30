package org.xml.core;

import android.util.Log;
import android.util.Xml;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;
import org.xml.annotation.XmlValue;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/1/30.
 */
public class XmlWriter {

    public void toXml(Object object, OutputStream outputStream) throws IOException, IllegalAccessException {
        if (outputStream == null) return;
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outputStream, "UTF-8");
        serializer.startDocument("UTF-8", null);
        writeTag(object, getTag(object.getClass()), serializer);
        serializer.endDocument();
    }

    private void writeTag(Object object, String mainTag, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        if (mainTag == null) {
            //value
            mainTag = cls.getSimpleName();
        }
        serializer.startTag(null, mainTag);
        writeAttributes(object, serializer);
        writeText(object, serializer);
        //
        Field[] fields = Reflect.getFileds(cls);
        Log.v("xml", mainTag + " fileds=" + fields.length);
        for (Field field : fields) {
            if(XmlUtil.isXmlAttribute(field))
                continue;
            if(XmlUtil.isXmlValue(field))
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
            Log.v("xml", field.getType() + ":" + field.getName());
            if (Reflect.isNormal(field.getType())) {
                Log.v("xml", mainTag + " normal sub tag " + field.getName());
                serializer.startTag(null, subTag);
                serializer.text("" + value);
                serializer.endTag(null, subTag);
            } else {
                Log.v("xml", mainTag + " other sub tag " + field.getName());
                writeTag(value, subTag, serializer);
            }
        }
        //
        serializer.endTag(null, mainTag);
    }

    private void writeAttributes(Object object, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if(XmlUtil.isXmlTag(field))
                continue;
            if(XmlUtil.isXmlValue(field))
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
            serializer.attribute(null, subTag, "" + val);
        }
    }

    private void writeText(Object object, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if(XmlUtil.isXmlAttribute(field))
                continue;
            if(XmlUtil.isXmlTag(field))
                continue;
            XmlValue xmlValue = field.getAnnotation(XmlValue.class);
            Reflect.accessible(field);
            if (xmlValue != null) {
                serializer.text("" + field.get(object));
            }
        }
    }

    private String getTag(Class<?> cls) {
        XmlTag xmlTag = cls.getAnnotation(XmlTag.class);
        if (xmlTag != null) {
            //value
            return xmlTag.value();
        } else {
            return cls.getSimpleName();
        }
    }
}
