package org.xml.core;

import android.util.Log;

import com.uutils.xml2object.BuildConfig;

import org.xml.annotation.XmlTag;
import org.xml.bean.Tag;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * {@link org.xml.bean.Tag } 转对象
 */
public class XmlReader extends IXml {
    static final String DEF_ENCODING = "UTF-8";

    public <T> T fromTag(Tag tag, Class<T> pClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return any(tag, pClass, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T array(Tag tag, Class<T> pClass, Object object)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (tag == null) {
            return null;
        }
        int count = tag.size();
        T t;
        if (object != null) {
            t = (T) object;
        } else {
            t = (T) Array.newInstance(pClass, count);
        }
        for (int i = 0; i < count; i++) {
            Array.set(t, i, any(tag.get(i), pClass.getComponentType(), null));
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T map(List<Tag> tags, Class<T> pClass, Object object, Class<?>[] subClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (tags == null || subClass == null || subClass.length < 2) {
            return null;
        }
        T t = object == null ? Reflect.create(pClass) : (T) object;
        if (BuildConfig.DEBUG)
            Log.v("xml", " put " + subClass[0] + "," + subClass[1] + " size=" + tags.size());
        for (Tag tag : tags) {
            Tag tk = tag.get(MAP_KEY);
            Object k = any(tk, subClass[0], null);
            Tag tv = tag.get(MAP_VALUE);
            Object v = any(tv, subClass[1], null);
            if (BuildConfig.DEBUG) {
                Log.v("xml", tag.getName() + " put " + (tk != null) + "=" + (tv != null));
                Log.v("xml", tag.getName() + " put " + k + "=" + v);
            }
            Reflect.call(t, "put", k, v);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T list(List<Tag> tags, Class<T> pClass, Object object, Class<?> subClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (tags == null) {
            return null;
        }
        if (BuildConfig.DEBUG)
            Log.d("xml", "list " + subClass);
        T t = object == null ? Reflect.create(pClass) : (T) object;
        for (Tag tag : tags) {
            tag.setClass(subClass);
            Reflect.call(t, "add", any(tag, subClass, null));
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T any(Tag tag, Class<T> pClass, Object object)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (tag == null) {
            return null;
        }
        if (tag.isArray()) {
            if (BuildConfig.DEBUG)
                Log.d("xml", "create array " + tag.getName() + " " + pClass);
            return array(tag, pClass, null);
        } else if (tag.isList()) {
            if (BuildConfig.DEBUG)
                Log.d("xml", "create list " + tag.getName() + " " + pClass);
            return list(tag.getTags(), pClass, null, getListClass(pClass));
        } else if (tag.isMap()) {
            if (BuildConfig.DEBUG)
                Log.d("xml", "create map " + tag.getName() + " " + pClass);
            return map(tag.getTags(), pClass, null, getMapClass(pClass));
        } else if (Reflect.isNormal(pClass)) {
            if (BuildConfig.DEBUG)
                Log.d("xml", "create normal " + tag.getName() + " " + pClass);
            if (object == null) {
                try {
                    object = Reflect.wrapper(pClass, tag.getText());
                } catch (Throwable e) {
                }
            }
            return (T) object;
        } else {
            if (BuildConfig.DEBUG)
                Log.d("xml", "create other " + tag.getName() + " " + pClass);
            T t = object == null ? Reflect.create(pClass) : (T) object;
            //attr
            for (Map.Entry<String, String> e : tag.attributes.entrySet()) {
                setAttribute(t, e.getKey(), e.getValue());
            }
            setText(t, tag.getText());
            int count = tag.size();
            List<String> oldtags = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Tag _tag = tag.get(i);
                if (_tag.isArray()) {
                    if (oldtags.contains(_tag.getName()))
                        continue;
                    oldtags.add(_tag.getName());
                    Field field = Reflect.getFiled(pClass, _tag.getName());
                    if (field != null)
                        Reflect.set(field, t, array(_tag, field.getType(), Reflect.get(field, t)));
                } else if (_tag.isList()) {
                    if (oldtags.contains(_tag.getName()))
                        continue;
                    oldtags.add(_tag.getName());
                    Field field = Reflect.getFiled(pClass, _tag.getName());
                    Class<?> subClass =getListClass(field);
                    if (field != null)
                        Reflect.set(field, t, list(tag.getList(_tag.getName()), field.getType(), Reflect.get(field, t), subClass));
                } else if (_tag.isMap()) {
                    if (oldtags.contains(_tag.getName()))
                        continue;
                    oldtags.add(_tag.getName());
                    Field field = Reflect.getFiled(pClass, _tag.getName());
                    if (field != null)
                        Reflect.set(field, t, map(tag.getList(_tag.getName()), field.getType(), Reflect.get(field, t), getMapClass(field)));
                } else {
                    if (oldtags.contains(_tag.getName()))
                        continue;
                    oldtags.add(_tag.getName());
                    Field field = Reflect.getFiled(pClass, _tag.getName());
                    if (field != null)
                        Reflect.set(field, t, any(_tag, field.getType(), Reflect.get(field, t)));
                }
            }
            return t;
        }
    }


    private Class<?> getListClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
        XmlTag xmlTag = cls.getAnnotation(XmlTag.class);
        if (xmlTag != null) {
            if (xmlTag.type() != null) {
                return xmlTag.type();
            }
        } else {
            if (BuildConfig.DEBUG)
                Log.w("xml", cls + " not's xmltag");
        }
        return Object.class;
    }

    private Class<?>[] getMapClass(AnnotatedElement cls) {
        if (cls == null)
            return new Class[]{Object.class, Object.class};
        XmlTag xmlTag = cls.getAnnotation(XmlTag.class);
        Class<?> kclass = Object.class;
        Class<?> vclass = Object.class;
        if (xmlTag != null) {
            if (xmlTag.keyType() != null) {
                kclass = xmlTag.keyType();
            }
            if (xmlTag.valueType() != null) {
                vclass = xmlTag.valueType();
            }
        }
        return new Class[]{kclass, vclass};
    }

    private void setAttribute(Object object, String tag, Object value)
            throws IllegalAccessException {
        if (object == null || tag == null) return;
        Field[] fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (isXmlAttribute(field)) {
                String name = getAttributeName(field, field.getName());
                if (tag.equals(name)) {
                    Reflect.set(field, object, value);
                    break;
                }
            }
        }
    }

    private void setText(Object object, Object value)
            throws IllegalAccessException {
        if (object == null) return;
        Field[] fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (isXmlValue(field)) {
                Reflect.set(field, object, value);
                break;
            }
        }
    }
}
