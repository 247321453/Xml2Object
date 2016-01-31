package org.xml.core;

import android.util.Log;

import org.xml.annotation.XmlTag;
import org.xml.bean.Tag;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class XmlReader extends IXml {

    //region read tag
    public Tag read(Class<?> tClass, InputStream inputStream) {
        if (inputStream == null) return null;
        XmlPullParser xmlParser = android.util.Xml.newPullParser();
        List<Tag> tagList = new ArrayList<>();
        int depth = -1;
        tagList.clear();
        Tag mTag = new Tag(getTagName(tClass, tClass.getSimpleName()));
        mTag.setClass(tClass);
        tagList.add(mTag);

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
                        } else if (depth != d) {
                            Tag p;
                            if (d - 1 >= 0 && d - 1 < tagList.size())
                                p = tagList.get(d - 1);
                            else
                                p = null;

                            mTag = new Tag(tag);
                            mTag.setClass(findClass(p, tag));
                            if (p != null) {
                                p.add(mTag);
                                Log.d("xml", p.getName() + " add " + tag);
                            } else {
                                Log.d("xml", "add " + tag);
                            }
                            tagList.add(mTag);
                            Log.d("xml", "add " + tag);
                        }
                        depth = d;
                        Log.v("xml", "mDepth=" + d);
                        int count = xmlParser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String k = xmlParser.getAttributeName(i);
                            String v = xmlParser.getAttributeValue(i);
                            mTag.attributes.put(k, v);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (mTag != null) {
                            mTag.setValue(xmlParser.getText());
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
        return tagList.get(0);
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
//
//    private Tag createSubTag(Class<?> cls, String name)
//            throws
//            NoSuchFieldException, IllegalAccessException,
//            InstantiationException, InvocationTargetException {
//        Tag tag = new Tag(name);
//        if (cls == null || name == null) return null;
//        Field[] fields = Reflect.getFileds(cls);
//        Field tfield = null;
//        for (Field field : fields) {
//            if (!isXmlTag(field))
//                continue;
//            XmlTag xmltag = field.getAnnotation(XmlTag.class);
//            if (xmltag != null) {
//                if (name.equals(xmltag.value())) {
//                    tfield = field;
//                    break;
//                }
//            }
//        }
//        if (tfield == null) {
//            tfield = Reflect.getFiled(cls, name);
//        }
//        if (tfield != null) {
//            Log.d("xml", "create find " + name);
//            setTagbyField(tfield, tag);
//            return tag;
//        }
//        Log.d("xml", "create no find " + tag);
//        return tag;
//    }
//
//    private void setTagbyField(Field tfield, Tag tag) {
//        if (tfield != null) {
//            tag.setClass(tfield.getType());
//            if (tfield.getType().isArray()) {
//                tag.setIsArray(true);
//            } else if (isXmlList(tfield)) {
//                XmlList xmlList = tfield.getAnnotation(XmlList.class);
//                tag.setIsArray(true);
//                tag.setSubClasss(xmlList.value());
//            } else if (isXmlMap(tfield)) {
//                XmlMap xmlMap = tfield.getAnnotation(XmlMap.class);
//                tag.setIsMap(true);
//                tag.setSubClasss(xmlMap.value());
//            } else {
//
//            }
//        }
//    }
}
