package org.xml.core;

import android.util.Log;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;
import org.xml.annotation.XmlValue;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class XmlReader {

    public <T> T toObject(Class<T> tClass, InputStream inputStream)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        XmlPullParser xmlParser = android.util.Xml.newPullParser();
        T t = Reflect.create(tClass);
        try {
            HashMap<Integer, Object> objectHashMap = new HashMap<>();
            xmlParser.setInput(inputStream, "utf-8");
            Object object = null;
            String tag = null;
            int depth = -1;
            // 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType = xmlParser.getEventType();
            // 一直循环，直到文档结束
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        //属性
                        tag = xmlParser.getName();
                        int d = xmlParser.getDepth();
                        Log.d("xml", "depth=" + d);
                        Object p = objectHashMap.get(Integer.valueOf(d - 1));
                        object = (depth < 0) ? t : createSubTag(p, tag);
                        if (object == null) {
                            Log.w("xml", "create fail " + tag);
                        }
                        if (d != depth) {
                            depth = d;
                            objectHashMap.put(d, object);
                        }
                        if (p != null)
                            setSubTag(p, object, tag);

                        int count = xmlParser.getAttributeCount();
                        Log.d("xml", "set attribute " + tag);
                        for (int i = 0; i < count; i++) {
                            setAttributes(object,
                                    xmlParser.getAttributeName(i),
                                    xmlParser.getAttributeValue(i));
                        }
                        break;
                    case XmlPullParser.TEXT:
                        //text
                        String text = xmlParser.getText();
                        Log.d("xml", tag + " set text = " + text);
                        setText(object, text);
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 如果xml没有结束，则导航到下一个river节点
                evtType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e) {

                }
            }
        }
        return t;
    }

    private void setAttributes(Object object, String key, String value) throws NoSuchFieldException, IllegalAccessException {
        if (object == null || key == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            XmlAttribute xmltag = field.getAnnotation(XmlAttribute.class);
            if (xmltag != null) {
                if (key.equals(xmltag.value())) {
                    Reflect.set(field, object, value);
                    return;
                }
            }
        }
        //
        Reflect.set(Reflect.getFiled(cls, key), object, value);
    }

    private void setText(Object object, String value) throws NoSuchFieldException, IllegalAccessException {
        if (object == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            XmlValue xmltag = field.getAnnotation(XmlValue.class);
            if (xmltag != null) {
                Reflect.set(field, object, value);
                return;
            }
        }
        //
        Reflect.set(Reflect.getFiled(cls, "value"), object, value);
    }

    private Object createSubTag(Object parent, String tag) throws NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (parent == null || tag == null) return null;
        Class<?> cls = parent.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            XmlTag xmltag = field.getAnnotation(XmlTag.class);
            if (xmltag != null) {
                if (tag.equals(xmltag.value())) {
                    return field.getType().newInstance();
                }
            }
        }
        //

        Field field = Reflect.getFiled(cls, tag);
        if (field != null) {
            return Reflect.create(field.getType());
        }
        return null;
    }

    private void setSubTag(Object parent, Object object, String tag) throws NoSuchFieldException, IllegalAccessException {
        if (parent == null || tag == null) return;
        Class<?> cls = parent.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            XmlTag xmltag = field.getAnnotation(XmlTag.class);
            if (xmltag != null) {
                if (tag.equals(xmltag.value())) {
                    Reflect.set(field, parent, object);
                    return;
                }
            }
        }
        //
        Reflect.set(Reflect.getFiled(cls, tag), parent, object);
    }

    private String getTag(Field field) {
        XmlTag xmltag = field.getAnnotation(XmlTag.class);
        if (xmltag == null) {
            return field.getName();
        }
        return xmltag.value();
    }

    private String getTag(Class<?> cls) {
        XmlTag xmltag = cls.getAnnotation(XmlTag.class);
        if (xmltag == null) {
            return cls.getSimpleName();
        }
        return xmltag.value();
    }
}
