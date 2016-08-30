package net.kk.xml;

import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.internal.Reflect;
import net.kk.xml.internal.XmlConstructorAdapter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class XmlObjectReader {
    XmlReader reader;
    XmlOptions options;

    public XmlObjectReader(XmlReader reader) {
        this.reader = reader;
        this.options = reader.getOptions();
    }

    public Object read(XmlObject parentXmlObj, XmlObject xmlObject, Object init, Object parent) throws Exception {
        if (xmlObject == null) {
            return null;
        } else if (xmlObject.getType() == null) {
            if (reader.DEBUG)
                System.out.println(xmlObject.getName() + "'s type is null ");
            return null;
        }
        Class<?> pClass = xmlObject.getTClass();
        String name = xmlObject.getName();
        if (reader.isNormal(xmlObject.getType())) {
            if (reader.DEBUG)
                System.out.println("create normal " + xmlObject.getName() + " " + pClass);
            Object obj = null;
            try {
                obj = reader.getAdapter(pClass).toObject(pClass, xmlObject.getText());
            } catch (Throwable e) {
                obj = init;
            }
            return obj;
        } else if (pClass.isArray()) {
            List<XmlObject> xmlObjects = getItems(parentXmlObj, xmlObject.getType(), name);
            return array(xmlObjects, pClass, init, parent);
        } else if (Collection.class.isAssignableFrom(pClass)) {
            List<XmlObject> xmlObjects = getItems(parentXmlObj, xmlObject.getType(), name);
            return list(xmlObjects, pClass, init, parent, reader.getListClass(xmlObject.getType()));
        } else if (Map.class.isAssignableFrom(pClass)) {
            List<XmlObject> xmlObjects = getItems(parentXmlObj, xmlObject.getType(), name);
            return map(xmlObjects, pClass, init, parent, reader.getMapClass(xmlObject.getType()));
        } else {
            // attribute
            // text
            // sub tag
            if (reader.DEBUG)
                System.out.println("create other " + xmlObject.getName() + " " + pClass);
            return object(xmlObject, pClass, init, parent);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T object(XmlObject xmlObject, Class<T> pClass, Object object, Object parent) throws Exception {
        T t;
        if (object == null) {
            XmlConstructorAdapter tXmlConstructorAdapter = reader.getConstructor(pClass);
            t = tXmlConstructorAdapter.create(pClass, parent);
        } else {
            t = (T) object;
        }
        if(t == null){
            return null;
        }
        // attr
        List<XmlObject.XmlAttributeObject> xmlAttributeObjects = xmlObject.getAttributes();
        if (xmlAttributeObjects != null) {
            if (reader.DEBUG) {
                System.out.println(xmlObject.getName() + " attr = " + xmlObject.getAttributes().size());
            }
            for (XmlObject.XmlAttributeObject e : xmlAttributeObjects) {
                setAttribute(t, e.getNamespace(), e.getName(), e.getValue());
            }
        }
        setText(t, xmlObject.getText());
        int count = xmlObject.getChildCount();
        List<String> oldtags = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            XmlObject el = xmlObject.getChildAt(i);
            String name = el.getName();
            if (oldtags.contains(name))
                continue;
            Field field = reader.getTagFiled(pClass, name);
            if (field == null) {
                if (reader.DEBUG)
                    System.out.println("no find field " + name);
                continue;
            }
            if (isIgnore(field)) {
                continue;
            }
            if (options.isIgnoreStatic() && (field.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            el.setType(field);
            oldtags.add(name);
            Object val = Reflect.get(field, t);
            Object obj = read(xmlObject, el, val, t);
            if (obj != null && !Modifier.isFinal(field.getModifiers())) {
                Reflect.set(field, t, obj, reader.getOptions().isUseSetMethod());
            }
        }
        return t;
    }

    private void setAttribute(Object object, String namespace, String tag, String value) throws Exception {
        if (object == null || tag == null)
            return;
        if (reader.DEBUG)
            System.out.println("np=" + namespace + ",name=" + tag + ", value=" + value);
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            String np = reader.getNamespace(field);
            String name = reader.getAttributeName(field);
            if (tag.equals(name)
                    && ((namespace == null && np == null) || (namespace != null && namespace.equals(np)))) {
                Object val = reader.getAdapter(field.getType()).toObject(field.getType(), value);// Reflect.wrapper(field.getType(),
                // value);
                if (val != null) {
                    Reflect.set(field, object, val, reader.getOptions().isUseSetMethod());
                }
                if (reader.DEBUG)
                    System.out.println(tag + " set " + value);
                break;
            }
        }
    }

    private void setText(Object object, String value) throws Exception {
        if (object == null)
            return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (reader.isXmlElementText(field)) {
                Class<?> cls = field.getType();
                Object val = reader.getAdapter(cls).toObject(cls, value);
                if (val != null) {
                    Reflect.set(field, object, val, reader.getOptions().isUseSetMethod());
                }
                break;
            }
        }
    }

    private List<XmlObject> getItems(XmlObject xmlObject, AnnotatedElement field, String name) {
        List<XmlObject> xmlObjects;
        if (reader.getOptions().isSameAsList()) {
            // System.out.println(name+" -> "+xmlObject);
            xmlObjects = xmlObject.getSameChild(name);
        } else {
            String subtag = reader.getItemTagName(field);
            XmlObject subObject = xmlObject.getChild(name);
            if (subObject != null) {
                xmlObjects = subObject.getSameChild(subtag);
            } else {
                xmlObjects = null;
            }
        }
        return xmlObjects;
    }

    @SuppressWarnings("unchecked")
    private <T> T array(List<XmlObject> xmlObjects, Class<T> pClass, Object object, Object parent) throws Exception {
        if (xmlObjects == null) {
            return null;
        }
        Class<?> sc = pClass.getComponentType();
        int count = xmlObjects.size();
        T t;
        if (object != null) {
            t = (T) object;
            //长度不够
        } else {
            t = (T) Array.newInstance(sc, count);
        }
        Integer integer = null;
        try {
            integer = (Integer) Reflect.getFieldValue(pClass, "length", t);
        } catch (Exception e) {

        }
        if (integer == null || integer < count) {
            t = (T) Array.newInstance(sc, count);
        }
        for (int i = 0; i < count; i++) {
            XmlObject xmlObject = xmlObjects.get(i);
            xmlObject.setType(sc);
            Object o = read(null, xmlObject, null, parent);
            if (o != null) {
                Array.set(t, i, o);
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T map(List<XmlObject> xmlObjects, Class<T> pClass, Object object, Object parent, Class<?>[] subClass)
            throws Exception {
        if (xmlObjects == null || subClass == null || subClass.length < 2) {
            return null;
        }
        T t;
        if (object == null) {
            t = (T) Reflect.createMap(pClass, subClass[0], subClass[1]);
            if (reader.DEBUG)
                System.out.println("create map " + pClass.getName());
        } else {
            t = (T) object;
        }
        if (t == null)
            return t;
        if (reader.DEBUG)
            System.out.println(" put " + subClass[0] + "," + subClass[1] + " getChildCount=" + xmlObjects.size());
        for (XmlObject xmlObject : xmlObjects) {
            Class<?> kc = subClass[0];
            XmlObject tk = xmlObject.getChild(XmlElementMap.KEY);
            tk.setType(kc);
            Object k = read(xmlObject, tk, null, parent);
            Class<?> vc = subClass[1];
            XmlObject tv = xmlObject.getChild(XmlElementMap.VALUE);
            tv.setType(vc);
            Object v = read(xmlObject, tv, null, parent);
            if (reader.DEBUG) {
                System.out.println(xmlObject.getName() + " put " + (tk != null) + "=" + (tv != null));
                System.out.println(xmlObject.getName() + " put " + k + "=" + v);
            }
            if (k != null)
                Reflect.call(t.getClass(), t, "put", k, v);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T list(List<XmlObject> xmlObjects, Class<T> pClass, Object object, Object parent, Class<?> subClass) throws Exception {
        if (xmlObjects == null) {
            return null;
        }
        if (reader.DEBUG)
            System.out.println("list " + subClass.getName());
        T t;
        if (object == null) {
            t = (T) Reflect.createCollection(pClass, subClass);
            if (reader.DEBUG)
                System.out.println("create list " + pClass.getName());
        } else {
            t = (T) object;
        }
        if (t != null) {
            // 多种派生类
            // boolean d = XmlClassSearcher.class.isAssignableFrom(subClass);
            for (XmlObject xmlObject : xmlObjects) {
                Class<?> sc = subClass;
                xmlObject.setType(sc);
                Object sub = read(null, xmlObject, null, parent);
                if (sub != null)
                    Reflect.call(t.getClass(), t, "add", sub);
                else {
                    if (reader.DEBUG)
                        System.out.println(xmlObject.getName() + "@" + sc.getName() + " is null");
                }
            }
        }
        return t;
    }

    protected boolean isIgnore(Field field) {
        Class<?> type = Reflect.wrapper(field.getType());
        if (type.isArray()) {
            return options.isIgnore(type.getComponentType());
        } else if (List.class.isAssignableFrom(type)) {
            return options.isIgnore(reader.getListClass(field));
        } else if (Map.class.isAssignableFrom(type)) {
            Class<?>[] classes = reader.getMapClass(field);
            return options.isIgnore(classes[0]) || options.isIgnore(classes[1]);
        } else {
            return options.isIgnore(type);
        }
    }
}
