package org.xml.core;

import android.util.Log;

import com.uutils.xml2object.BuildConfig;

import org.xml.annotation.XmlTag;
import org.xml.bean.Tag;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XmlConvert extends IXml {
    //region to tag

    /**
     * 从流转换为tag对象
     *
     * @param tClass      解析的类
     * @param inputStream 输入流
     * @return tag对象
     */
    public Tag toTag(Class<?> tClass, InputStream inputStream) {
        if (inputStream == null) return null;
        XmlPullParser xmlParser = android.util.Xml.newPullParser();
        Map<Integer, Tag> tagMap = new HashMap<>();
        int depth = -1;
        Tag mTag = new Tag(getTagName(tClass, tClass.getSimpleName()));
        mTag.setClass(tClass);
        tagMap.put(1, mTag);

        try {
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                // 一直循环，直到文档结束
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        //属性
                        String tag = xmlParser.getName();
                        int d = xmlParser.getDepth();
                        if (depth < 0) {
                            //
                        } else {
                            Tag p = tagMap.get(d - 1);
                            mTag = new Tag(tag);
                            mTag.setClass(findClass(p, tag));
                            if (p != null) {
                                p.add(mTag);
                            } else {
                            }
                            tagMap.put(d, mTag);
                        }
                        depth = d;
                        int count = xmlParser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String k = xmlParser.getAttributeName(i);
                            String v = xmlParser.getAttributeValue(i);
                            mTag.attributes.put(k, v);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (mTag != null) {
                            mTag.setText(xmlParser.getText());
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
        return tagMap.get(1);
    }

    //ednregion
    private Class<?> findClass(Tag p, String name) {
        if (p == null || p.getTClass() == null) return Object.class;
        Field[] fields = Reflect.getFileds(p.getTClass());
        Field tfield = null;
        for (Field field : fields) {
            if (!isXmlTag(field))
                continue;
            XmlTag xmltag = field.getAnnotation(XmlTag.class);
            if (xmltag != null) {
                if (name.equals(xmltag.value())) {
                    tfield = field;
                    break;
                }
            }
        }
        if (tfield == null) {
            tfield = Reflect.getFiled(p.getTClass(), name);
        }
        return tfield != null ? tfield.getType() : Object.class;
    }

    /**
     * 从java对象转换为tag对象
     *
     * @param object java对象
     * @param name   元素名
     * @return tag对象
     */
    public Tag toTag(Object object, String name) throws IllegalAccessException {
        Tag root = new Tag(name);
        if (object == null) return root;
        Class<?> cls = object.getClass();
        root.setClass(cls);
        if (name == null) {
            name = getTagName(cls, cls.getSimpleName());
            root.setName(name);
        }
        if (Reflect.isNormal(cls)) {
            root.setText(toString(object));
        } else if (cls.isArray()) {
            root.addAll(array(object, name));
        } else if (object instanceof Map) {
            root.addAll(map(object, name));
        } else if (object instanceof Collection) {
            root.addAll(list(object, name));
        } else {
            writeAttributes(object, root);
            writeText(object, root);
            writeSubTag(object, root);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Tag> map(Object object, String name) throws IllegalAccessException {
        ArrayList<Tag> list = new ArrayList<>();
        if (object == null) {
            return list;
        }
        Object set = Reflect.call(object, "entrySet");
        if (set instanceof Set) {
            Set<Map.Entry<?, ?>> sets = (Set<Map.Entry<?, ?>>) set;
            for (Map.Entry<?, ?> e : sets) {
                if (BuildConfig.DEBUG)
                    Log.v("xml", "map " + e);
                Tag tag = new Tag(name);
                tag.add(toTag(e.getKey(), MAP_KEY));
                tag.add(toTag(e.getValue(), MAP_VALUE));
                list.add(tag);
            }
        }
        return list;
    }

    private ArrayList<Tag> array(Object object, String name) throws IllegalAccessException {
        ArrayList<Tag> list = new ArrayList<>();
        if (object != null) {
            int count = Array.getLength(object);
            for (int i = 0; i < count; i++) {
                list.add(toTag(Array.get(object, i), name));
            }
        }
        return list;
    }

    private ArrayList<Tag> list(Object object, String name) throws IllegalAccessException {
        ArrayList<Tag> list = new ArrayList<>();
        if (object != null) {
            Object[] objs = (Object[]) Reflect.call(object, "toArray");
            if (objs != null) {
                for (Object o : objs) {
                    list.add(toTag(o, name));
                }
            }
        }
        return list;
    }

    //region write
    private void writeAttributes(Object object, Tag tag) throws IllegalAccessException {
        if (object == null || tag == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if (!isXmlAttribute(field))
                continue;
            String subTag = getAttributeName(field, field.getName());
            Reflect.accessible(field);
            Object val = field.get(object);
            if (BuildConfig.DEBUG)
                Log.v("xml", subTag + "=" + val);
            tag.attributes.put(subTag, toString(val));
        }
    }

    private void writeSubTag(Object object, Tag tag) throws IllegalAccessException {
        if (object == null) return;
        Field[] fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (isXmlIgnore(field))
                continue;
            if (isXmlAttribute(field))
                continue;
            if (isXmlValue(field))
                continue;
            String name = getTagName(field, field.getName());
            Class<?> cls = field.getType();
            Reflect.accessible(field);
            Object val = field.get(object);
            if (val != null) {
                if (val instanceof Map) {
                    tag.addAll(map(val, name));
                } else if (val instanceof Collection) {
                    tag.addAll(list(val, name));
                } else {
                    tag.add(toTag(val, name));
                }
            }
        }
    }

    private void writeText(Object object, Tag tag) throws IllegalAccessException {
        if (object == null || tag == null) return;
        Class<?> cls = object.getClass();
        Field[] fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if (isXmlValue(field)) {
                Reflect.accessible(field);
                Object val = field.get(object);
                tag.setText(toString(val));
                break;
            }
        }
    }
    //endregion
}
