package org.xml.core;

import android.util.Log;

import org.xml.annotation.XmlElement;

import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * {@link Element } 转对象
 */
public class XmlReader extends IXml {
    protected XmlConvert mXmlConvert;

    public XmlReader() {
        mXmlConvert = new XmlConvert();
    }

    /***
     * @param inputStream 输入流
     * @param pClass      类
     * @param <T>         类型
     * @return 对象
     * @throws IllegalAccessException    异常1
     * @throws InstantiationException    异常2
     * @throws InvocationTargetException 异常3
     */
    public <T> T from(InputStream inputStream, Class<T> pClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Element tag = mXmlConvert.toTag(pClass, inputStream);
        if (IXml.DEBUG)
            Log.d("xml", "form " + tag);
        return any(tag, pClass, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T array(Element element, Class<T> pClass, Object object)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (element == null) {
            return null;
        }
        int count = element.size();
        T t;
        if (object != null) {
            t = (T) object;
        } else {
            t = (T) Array.newInstance(pClass, count);
        }
        for (int i = 0; i < count; i++) {
            Array.set(t, i, any(element.get(i), pClass.getComponentType(), null));
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T map(List<Element> elements, Class<T> pClass, Object object, Class<?>[] subClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (elements == null || subClass == null || subClass.length < 2) {
            return null;
        }
        T t = object == null ? Reflect.create(pClass, subClass) : (T) object;
        if (t == null) return t;
        if (IXml.DEBUG)
            Log.v("xml", " put " + subClass[0] + "," + subClass[1] + " size=" + elements.size());
        for (Element element : elements) {
            Element tk = element.get(MAP_KEY);
            Object k = any(tk, subClass[0], null);
            Element tv = element.get(MAP_VALUE);
            Object v = any(tv, subClass[1], null);
            if (IXml.DEBUG) {
                Log.v("xml", element.getName() + " put " + (tk != null) + "=" + (tv != null));
                Log.v("xml", element.getName() + " put " + k + "=" + v);
            }
            Reflect.call(t.getClass(), t, "put", k, v);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T list(List<Element> elements, Class<T> pClass, Object object, Class<?> subClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (elements == null) {
            return null;
        }
        if (IXml.DEBUG)
            Log.d("xml", "list " + subClass.getName());
        T t = object == null ? Reflect.create(pClass, subClass) : (T) object;
        if (t != null) {
            for (Element element : elements) {
                element.setClass(subClass);
                Reflect.call(t.getClass(), t, "add", any(element, subClass, null));
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T any(Element element, Class<T> pClass, Object object)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (element == null) {
            return null;
        }
        if (element.isArray()) {
            if (IXml.DEBUG)
                Log.v("xml", "create array " + element.getName() + " " + pClass);
            return array(element, pClass, null);
        } else if (element.isList()) {
            if (IXml.DEBUG)
                Log.v("xml", "create list " + element.getName() + " " + pClass);
            return list(element.getElements(), pClass, null, getListClass(pClass));
        } else if (element.isMap()) {
            if (IXml.DEBUG)
                Log.v("xml", "create map " + element.getName() + " " + pClass);
            return map(element.getElements(), pClass, null, getMapClass(pClass));
        } else if (Reflect.isNormal(pClass)) {
            if (IXml.DEBUG)
                Log.v("xml", "create normal " + element.getName() + " " + pClass);
            if (object == null) {
                try {
                    object = Reflect.wrapper(pClass, element.getText());
                } catch (Throwable e) {
                }
            }
            return (T) object;
        } else {
            if (IXml.DEBUG)
                Log.d("xml", "create other " + element.getName() + " " + pClass);
            return object(element, pClass, object);
        }
    }

    private <T> T  object(Element element, Class<T> pClass, Object object) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t = object == null ? Reflect.create(pClass) : (T) object;
        //attr
        for (Map.Entry<String, String> e : element.attributes.entrySet()) {
            setAttribute(t, e.getKey(), e.getValue());
        }
        setText(t, element.getText());
        int count = element.size();
        List<String> oldtags = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Element _element = element.get(i);
            if (_element.isArray()) {
                if (oldtags.contains(_element.getName()))
                    continue;
                oldtags.add(_element.getName());
                Field field = Reflect.getFiled(pClass, _element.getName());
                if (field != null)
                    Reflect.set(field, t, array(_element, field.getType(), Reflect.get(field, t)));
            } else if (_element.isList()) {
                if (oldtags.contains(_element.getName()))
                    continue;
                oldtags.add(_element.getName());
                Field field = Reflect.getFiled(pClass, _element.getName());
                Class<?> subClass = getListClass(field);
                if (field != null)
                    Reflect.set(field, t, list(element.getElementList(_element.getName()), field.getType(), Reflect.get(field, t), subClass));
            } else if (_element.isMap()) {
                if (oldtags.contains(_element.getName()))
                    continue;
                oldtags.add(_element.getName());
                Field field = Reflect.getFiled(pClass, _element.getName());
                if (field != null)
                    Reflect.set(field, t, map(element.getElementList(_element.getName()), field.getType(), Reflect.get(field, t), getMapClass(field)));
            } else {
                if (oldtags.contains(_element.getName()))
                    continue;
                oldtags.add(_element.getName());
                Field field = Reflect.getFiled(pClass, _element.getName());
                if (field != null)
                    Reflect.set(field, t, any(_element, field.getType(), Reflect.get(field, t)));
            }
        }
        return t;
    }


    private Class<?> getListClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        if (xmlElement != null) {
            if (xmlElement.type() != null) {
                return xmlElement.type();
            }
        } else {
            if (IXml.DEBUG)
                Log.w("xml", cls + " not's xmltag");
        }
        return Object.class;
    }

    private Class<?>[] getMapClass(AnnotatedElement cls) {
        if (cls == null)
            return new Class[]{Object.class, Object.class};
        XmlElement xmlElement = cls.getAnnotation(XmlElement.class);
        Class<?> kclass = Object.class;
        Class<?> vclass = Object.class;
        if (xmlElement != null) {
            if (xmlElement.keyType() != null) {
                kclass = xmlElement.keyType();
            }
            if (xmlElement.valueType() != null) {
                vclass = xmlElement.valueType();
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
