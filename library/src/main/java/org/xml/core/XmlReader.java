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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlReader {

    public <T> T toObject(Class<T> tClass, InputStream inputStream)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        XmlPullParser xmlParser = android.util.Xml.newPullParser();
        T t = Reflect.create(tClass);
        try {
            HashMap<Integer, Object> objectHashMap = new HashMap<>();
            xmlParser.setInput(inputStream, "utf-8");
            Object object = null;
            Object subobject = null;
            String tag = null;
            int depth = -1;
            boolean isList = false;
            boolean isArray = false;
            boolean isMap = false;
            // 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType = xmlParser.getEventType();
            // 一直循环，直到文档结束
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        //属性
                        String _tag = xmlParser.getName();
                        if ((isArray || isMap || isList) && tag.equalsIgnoreCase(_tag)) {
                            //不用创建
                        } else {
                            tag = _tag;
                            int d = xmlParser.getDepth();
                            Log.v("xml", "depth=" + d);
                            Object p = objectHashMap.get(Integer.valueOf(d - 1));
                            object = (depth < 0) ? t : createSubTag(p, tag);
                            if (object == null) {
                                Log.w("xml", "create fail " + tag);
                            } else {
                                Log.i("xml", "create ok " + tag);
                                isArray = object.getClass().isArray();
                                isList = object instanceof List;
                                isMap = object instanceof Map;
                            }
                            if (d != depth) {
                                depth = d;
                                objectHashMap.put(d, object);
                                Log.d("xml", "put depth " + d);
                            }
                            if (p != null)
                                setSubTag(p, object, tag);
                        }
                        if (isArray) {
                            subobject = Reflect.create(object.getClass().getComponentType());
                            //TODO 添加元素
                        }
                        if (isList) {
                            subobject = Reflect.create(getListClass(object.getClass()));
                            Reflect.call(object, "add", subobject);
                        }
                        if (isMap) {
                            //TODO 创建，添加
                        }
                        int count = xmlParser.getAttributeCount();
                        Log.d("xml", "set attribute " + tag);
                        for (int i = 0; i < count; i++) {
                            String k = xmlParser.getAttributeName(i);
                            String v = xmlParser.getAttributeValue(i);
                            if (isArray || isList) {
                                setAttributes(subobject, k, v);
                            } else {
                                setAttributes(object, k, v);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        //TODO:数组,List,Map
                        String text = xmlParser.getText();
                        Log.d("xml", tag + " set text = " + text);
                        if (isArray || isList) {
                            setText(subobject, text);
                        } else {
                            setText(object, text);
                        }
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

    private Class<?> getListClass(Class<?> cls) throws NoSuchMethodException {
        Method method = cls.getMethod("get", new Class<?>[0]);
        return method == null ? Object.class : method.getReturnType();
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
                    return Reflect.create(field.getType());
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
