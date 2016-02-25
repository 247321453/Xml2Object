package org.xml.core;

import android.util.Log;

import org.xml.annotation.XmlElementMap;
import org.xml.convert.Reflect;
import org.xml.convert.TypeToken;

import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/***
 * {@link TypeToken } 转对象
 */
public class XmlReader {
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
    public <T> T from(InputStream inputStream, Class<T> pClass, String encoding)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        TypeToken tag = mXmlConvert.toTag(pClass, inputStream, encoding);
        if (KXml.DEBUG)
            Log.d("xml", "form " + tag);
        return any(tag, pClass, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T array(List<TypeToken> typeTokens, Class<T> pClass, Object object)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (typeTokens == null) {
            return null;
        }
        Class<?> sc = pClass.getComponentType();
        int count = typeTokens.size();
        T t;
        if (object != null) {
            t = (T) object;
        } else {
            t = (T) Array.newInstance(sc, count);
        }
        if (KXml.DEBUG)
            Log.i("xml", "create array " + pClass.getName() + " sub=" + sc);
//        boolean d = XmlClassSearcher.class.isAssignableFrom(subClass);

        for (int i = 0; i < count; i++) {
            TypeToken typeToken = typeTokens.get(i);
//            if (d) {
//                sc = mXmlConvert.getSubClass(subClass, element);
//            } else {
//                sc = subClass;
//            }

            Object o = any(typeToken, sc, null);
            if (o != null) {
                if (KXml.DEBUG)
                    Log.v("xml", "child = " + sc + "/" + o.getClass());
                Array.set(t, i, o);
            } else {
                if (KXml.DEBUG)
                    Log.w("xml", "child is null " + typeToken.getName());
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T map(List<TypeToken> typeTokens, Class<T> pClass, Object object, AnnotatedElement field)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (typeTokens == null || field == null) {
            return null;
        }
        Class<?>[] subClass = KXml.getMapClass(field);
        T t;
        if (object == null) {
            t = Reflect.create(pClass, subClass);
            if (KXml.DEBUG)
                Log.v("xml", "create map " + pClass.getName());
        } else {
            t = (T) object;
        }
        if (t == null) return t;
        if (KXml.DEBUG)
            Log.v("xml", " put " + subClass[0] + "," + subClass[1] + " size=" + typeTokens.size());
//        boolean dkey = XmlClassSearcher.class.isAssignableFrom(subClass[0]);
//        boolean dval = XmlClassSearcher.class.isAssignableFrom(subClass[1]);
        String keyName;
        String valueName;
        XmlElementMap xmlElementMap = field.getAnnotation(XmlElementMap.class);
        if (xmlElementMap == null) {
            keyName = KXml.MAP_KEY_NAME;
            valueName = KXml.MAP_VALUE_NAME;
        } else {
            keyName = xmlElementMap.keyName();
            valueName = xmlElementMap.valueName();
        }
        for (TypeToken typeToken : typeTokens) {
            Class<?> kc = subClass[0];
//            if (dkey) {
//                kc = mXmlConvert.getSubClass(subClass[0], element);
//            } else {
//                kc = subClass[0];
//            }
            TypeToken tk = typeToken.get(keyName);
            Object k = any(tk, kc, null);
            Class<?> vc = subClass[1];
//            if (dval) {
//                vc = mXmlConvert.getSubClass(subClass[1], element);
//            } else {
//                vc = subClass[0];
//            }
            TypeToken tv = typeToken.get(valueName);
            Object v = any(tv, vc, null);
            if (KXml.DEBUG) {
                Log.v("xml", typeToken.getName() + " put " + (tk != null) + "=" + (tv != null));
                Log.v("xml", typeToken.getName() + " put " + k + "=" + v);
            }
            if (k != null)
                Reflect.call(t.getClass(), t, "put", k, v);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T list(List<TypeToken> typeTokens, Class<T> pClass, Object object, Class<?> subClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (typeTokens == null) {
            return null;
        }
        if (KXml.DEBUG)
            Log.v("xml", "list " + subClass.getName());
        T t;
        if (object == null) {
            t = Reflect.create(pClass, subClass);
            if (KXml.DEBUG)
                Log.v("xml", "create list " + pClass.getName());
        } else {
            t = (T) object;
        }
        if (t != null) {
            //多种派生类
//            boolean d = XmlClassSearcher.class.isAssignableFrom(subClass);
            for (TypeToken typeToken : typeTokens) {
                Class<?> sc = subClass;
//                if (d) {
//                    sc = mXmlConvert.getSubClass(subClass, element);
//                    if (IXml.DEBUG)
//                        Log.v("xml", "child = " + sc);
//                } else {
//                    sc = subClass;
//                }
                typeToken.setType(sc);
                Object sub = any(typeToken, sc, null);
                if (sub != null)
                    Reflect.call(t.getClass(), t, "add", sub);
                else {
                    Log.w("xml", typeToken.getName() + "@" + sc.getName() + " is null");
                }
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T any(TypeToken typeToken, Class<T> pClass, Object object)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (typeToken == null) {
            return null;
        } else if (typeToken.getType() == null) {
            if (KXml.DEBUG)
                Log.w("xml", typeToken.getName() + " 's type is null ");
            return null;
        }
        if (Reflect.isNormal(pClass)) {
            if (KXml.DEBUG)
                Log.v("xml", "create normal " + typeToken.getName() + " " + pClass);
            try {
                object = Reflect.wrapper(pClass, typeToken.getText());
            } catch (Throwable e) {
            }
            return (T) object;
        } else {
            if (KXml.DEBUG)
                Log.v("xml", "create other " + typeToken.getName() + " " + pClass);
            return object(typeToken, pClass, object);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T object(TypeToken typeToken, Class<T> pClass, Object parent) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (Reflect.isNormal(pClass)) {
            if (KXml.DEBUG)
                Log.v("xml", "create normal " + typeToken.getName() + " " + pClass);
            return (T) Reflect.wrapper(pClass, typeToken.getText());
        }
        T t = (parent == null) ? Reflect.create(pClass) : (T) parent;
        //attr
        if (KXml.DEBUG) {
            Log.d("xml", typeToken.getName() + " attr = " + typeToken.getAttributes().size());
        }
        for (Map.Entry<String, String> e : typeToken.getAttributes().entrySet()) {
            setAttribute(t, e.getKey(), e.getValue());
        }
        setText(t, typeToken.getText());
        int count = typeToken.size();
        List<String> oldtags = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TypeToken el = typeToken.get(i);
            String name = el.getName();
            if (oldtags.contains(name))
                continue;
            Field field = Reflect.getTagFiled(pClass, name);
            if (field == null) {
                Log.w("xml", "no find field " + name);
                continue;
            }
            oldtags.add(name);
            Class<?> cls = field.getType();
            Object val = Reflect.get(field, t);
            Object obj = null;
            if (cls.isArray()) {
                obj = array(typeToken.getElementList(name), cls, val);
            } else if (Collection.class.isAssignableFrom(cls)) {
                obj = list(typeToken.getElementList(name), cls, val, KXml.getListClass(field));
            } else if (Map.class.isAssignableFrom(cls)) {
                obj = map(typeToken.getElementList(name), cls, val, field);
            } else {
                obj = any(el, cls, val);
            }
            if (!Modifier.isFinal(field.getModifiers())) {
                Reflect.set(field, t, obj);
            }
        }
        return t;
    }

    private void setAttribute(Object object, String tag, String value)
            throws IllegalAccessException {
        if (object == null || tag == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            String name = KXml.getAttributeName(field);
            if (tag.equals(name)) {
                Reflect.set(field, object, Reflect.wrapper(field.getType(), value));
                if (KXml.DEBUG)
                    Log.v("xml", tag + " set " + value);
                break;
            }
        }
    }

    private void setText(Object object, String value)
            throws IllegalAccessException {
        if (object == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (KXml.isXmlValue(field)) {
                Reflect.set(field, object, Reflect.wrapper(field.getType(), value));
                break;
            }
        }
    }
}
