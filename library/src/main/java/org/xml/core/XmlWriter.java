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
        writeTag(object, serializer);
        serializer.endDocument();
    }

    private void writeTag(Object object, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        XmlTag xmlTag = cls.getAnnotation(XmlTag.class);
        String mainTag;
        if (xmlTag != null) {
            //value
            mainTag = xmlTag.value();
        } else {
            mainTag = cls.getSimpleName();
        }
        serializer.startTag(null, mainTag);
        writeAttributes(object, serializer);
        writeText(object, serializer);
        //
        Field[] fields = Reflect.getFileds(cls);
        Log.v("xml", mainTag + " fileds=" + fields.length);
        for (Field field : fields) {
            xmlTag = field.getAnnotation(XmlTag.class);
            if(xmlTag==null){
                continue;
            }
            Reflect.accessible(field);
            Object value = field.get(object);
            //自定义类
            Log.v("xml", field.getType() + ":" + field.getName());
            if (Reflect.isNormal(field.getType())) {
                Log.v("xml", mainTag + " normal sub tag " + field.getName());
                String subTag = xmlTag.value();
                serializer.startTag(null, subTag);
                serializer.text("" + value);
                serializer.endTag(null, subTag);
            } else {
                Log.v("xml", mainTag + " other sub tag " + field.getName());
                writeTag(value, serializer);
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
            XmlAttribute xmlAttr = field.getAnnotation(XmlAttribute.class);
            XmlValue xmlValue = field.getAnnotation(XmlValue.class);
            XmlTag xmlTag = field.getAnnotation(XmlTag.class);
            Reflect.accessible(field);
            Object val = field.get(object);
            if (xmlAttr != null) {
                Log.d("xml", xmlAttr.value() + "=" + val);
                serializer.attribute(null, xmlAttr.value(), "" +val);
            } else if (xmlValue == null && xmlTag == null) {
                //value
                serializer.attribute(null, field.getName(), "" + field.get(object));
            }
        }
    }

    private void writeText(Object object, XmlSerializer serializer) throws IllegalAccessException, IOException {
        if (object == null || serializer == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            XmlValue xmlValue = field.getAnnotation(XmlValue.class);
            Reflect.accessible(field);
            if (xmlValue != null) {
                serializer.text("" + field.get(object));
            }
        }
    }
}
