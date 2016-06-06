package net.kk.xml.internal.bind;

import android.util.Log;

import net.kk.xml.XmlReader;
import net.kk.xml.XmlWriter;
import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.internal.XmlObject;
import net.kk.xml.internal.XmlOptions;
import net.kk.xml.internal.XmlTypeAdapter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectTypeAdapter implements XmlTypeAdapter<Object> {
    @Override
    public XmlObject write(XmlWriter writer, String name, AnnotatedElement element, Object object) throws Exception {
        if (name == null) {
            name = writer.getTagName(element);
        }
        if (name == null || element == null || object == null) return null;
        XmlObject root = new XmlObject(name);
        Class<?> cls = object.getClass();
        root.setType(cls);
        XmlOptions options = writer.getOptions();
        if (Reflect.isNormal(cls)) {
            root.setText(object.toString());
        } else if (cls.isArray()) {
            String subtag = options.isSameAsList() ? writer.getTagName(element) : writer.getItemTagName(element);
            root.addAll(array(writer, object, subtag));
        } else if (object instanceof Map) {
            String subtag = options.isSameAsList() ? writer.getTagName(element) : writer.getItemTagName(element);
            root.addAll(map(writer, object, cls, subtag));
        } else if (object instanceof Collection) {
            String subtag = options.isSameAsList() ? writer.getTagName(element) : writer.getItemTagName(element);
            root.addAll(list(writer, object, subtag));
        } else {
            writeText(writer, object, root);
            writeAttributes(writer, object, root);
            writeSubTag(writer, object, root);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<XmlObject> map(XmlWriter writer, Object object, Class<?> pClass, String name) throws Exception {
        ArrayList<XmlObject> list = new ArrayList<XmlObject>();
        if (object == null) {
            return list;
        }
        Object set = Reflect.call(object.getClass(), object, "entrySet");
        if (set instanceof Set) {
            Set<Map.Entry<?, ?>> sets = (Set<Map.Entry<?, ?>>) set;
            for (Map.Entry<?, ?> e : sets) {
                Object k = e.getKey();
                Object v = e.getValue();
                if (k == null) {
                    continue;
                }
                XmlObject xmlObject = new XmlObject(name);
                xmlObject.setType(pClass);
                xmlObject.setSubItem(true);
                Class kcls = k.getClass();
                XmlTypeAdapter kadapter = writer.getAdapter(kcls);
                xmlObject.add(kadapter.write(writer, XmlElementMap.KEY, kcls, k));
                if (v == null) {
                    xmlObject.add(new XmlObject(XmlElementMap.VALUE));
                } else {
                    Class cls = v.getClass();
                    XmlTypeAdapter adapter = writer.getAdapter(cls);
                    xmlObject.add(adapter.write(writer, XmlElementMap.VALUE, cls, v));
                }
                list.add(xmlObject);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private List<XmlObject> array(XmlWriter writer, Object object, String name) throws Exception {
        ArrayList<XmlObject> list = new ArrayList<XmlObject>();
        if (object != null) {
            int count = Array.getLength(object);
            for (int i = 0; i < count; i++) {
                Object obj = Array.get(object, i);
                if (obj != null) {
                    Class<?> cls = obj.getClass();
                    XmlTypeAdapter adapter = writer.getAdapter(cls);
                    if (adapter != null) {
                        //
                        XmlObject object1 = adapter.write(writer, name, cls, obj);
                        object1.setSubItem(true);
                        list.add(object1);
                    }
                }
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private List<XmlObject> list(XmlWriter writer, Object object, String name) throws Exception {
        ArrayList<XmlObject> list = new ArrayList<XmlObject>();
        if (object != null) {
            Object[] objs = (Object[]) Reflect.call(object.getClass(), object, "toArray");
            if (objs != null) {
                for (Object obj : objs) {
                    if (obj != null) {
                        Class<?> cls = obj.getClass();
                        XmlTypeAdapter adapter = writer.getAdapter(cls);
                        if (adapter != null) {
                            //
                            XmlObject object1 = adapter.write(writer, name, cls, obj);
                            object1.setSubItem(true);
                            list.add(object1);
                        }
                    }
                }
            }
        }
        return list;
    }

    private void writeText(XmlWriter writer, Object object, XmlObject xmlObject) throws IllegalAccessException {
        if (object == null || xmlObject == null) return;
        Class<?> cls = object.getClass();
        Collection<Field> fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if (writer.isXmlElementText(field)) {
                Reflect.accessible(field);
                Object val = field.get(object);
                xmlObject.setText(val == null ? "" : val.toString());
                break;
            }
        }
    }

    private void writeAttributes(XmlWriter writer, Object object, XmlObject parent) throws IllegalAccessException {
        if (object == null || parent == null) return;
        Class<?> cls = object.getClass();
        Collection<Field> fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            String subTag = writer.getAttributeName(field);
            if (subTag == null)
                continue;
            Reflect.accessible(field);
            Object val = field.get(object);
            parent.addAttribute(subTag, val == null ? "" : val.toString());
        }
    }

    private void writeSubTag(XmlWriter writer, Object object, XmlObject parent) throws Exception {
        if (object == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            String name = writer.getTagName(field);
            if (name == null)
                continue;
            Class<?> cls = field.getType();
            XmlTypeAdapter adapter = writer.getAdapter(cls);
            Reflect.accessible(field);
            Object val = field.get(object);
            XmlObject fobject = adapter.write(writer, name, field, val);
            if (fobject != null) {
                parent.add(fobject);
            }
        }
    }

    @Override
    public Object read(XmlReader reader, XmlObject parent, XmlObject xmlObject, Object init) throws Exception {
        if (xmlObject == null) {
            return null;
        } else if (xmlObject.getType() == null) {
            if (reader.DEBUG)
                Log.w("xml", xmlObject.getName() + "'s type is null ");
            return null;
        }
        Class<?> pClass = xmlObject.getTClass();
//        XmlOptions options = reader.getOptions();
        String name = xmlObject.getName();
        if (pClass.isArray()) {
            List<XmlObject> xmlObjects = getItems(reader, parent, xmlObject.getType(), name);
            return array(reader, xmlObjects, pClass, init);
        } else if (Collection.class.isAssignableFrom(pClass)) {
            List<XmlObject> xmlObjects = getItems(reader, parent, xmlObject.getType(), name);
            return list(reader, xmlObjects, pClass, init, reader.getListClass(xmlObject.getType()));
        } else if (Map.class.isAssignableFrom(pClass)) {
            List<XmlObject> xmlObjects = getItems(reader, parent, xmlObject.getType(), name);
            return map(reader, xmlObjects, pClass, init, reader.getMapClass(xmlObject.getType()));
        }
        if (Reflect.isNormal(pClass)) {
            if (reader.DEBUG)
                Log.v("xml", "create normal " + xmlObject.getName() + " " + pClass);
            Object obj = null;
            try {
                obj = Reflect.wrapper(pClass, xmlObject.getText());
            } catch (Throwable e) {
            }
            return obj;
        } else {
            //attribute
            //text
            //sub tag
            if (reader.DEBUG)
                Log.v("xml", "create other " + xmlObject.getName() + " " + pClass);
            return object(reader, xmlObject, pClass, init);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T object(XmlReader reader, XmlObject xmlObject, Class<T> pClass, Object parent) throws Exception {
        T t = (parent == null) ? Reflect.create(pClass) : (T) parent;
        //attr
        if (reader.DEBUG) {
            Log.d("xml", xmlObject.getName() + " attr = " + xmlObject.getAttributes().size());
        }
        for (Map.Entry<String, String> e : xmlObject.getAttributes().entrySet()) {
            setAttribute(reader, t, e.getKey(), e.getValue());
        }
        setText(reader, t, xmlObject.getText());
        int count = xmlObject.size();
        List<String> oldtags = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            XmlObject el = xmlObject.get(i);
            String name = el.getName();
            if (oldtags.contains(name))
                continue;
            Field field = reader.getTagFiled(pClass, name);
            if (field == null) {
                if (reader.DEBUG)
                    Log.w("xml", "no find field " + name);
                continue;
            }
            el.setType(field);
            oldtags.add(name);
            Class<?> cls = field.getType();
            Object val = Reflect.get(field, t);
            XmlTypeAdapter adapter = reader.getAdapter(cls);
            Object obj = adapter.read(reader, xmlObject, el, val);

            if (obj != null && !Modifier.isFinal(field.getModifiers())) {
                Reflect.set(field, t, obj, reader.getOptions().isUseSetMethod());
            }
        }
        return t;
    }

    private void setAttribute(XmlReader reader, Object object, String tag, String value)
            throws IllegalAccessException {
        if (object == null || tag == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            String name = reader.getAttributeName(field);
            if (tag.equals(name)) {
                Reflect.set(field, object, Reflect.wrapper(field.getType(), value), reader.getOptions().isUseSetMethod());
                if (reader.DEBUG)
                    Log.v("xml", tag + " set " + value);
                break;
            }
        }
    }

    private void setText(XmlReader reader, Object object, String value)
            throws IllegalAccessException {
        if (object == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (reader.isXmlElementText(field)) {
                Reflect.set(field, object, Reflect.wrapper(field.getType(), value), reader.getOptions().isUseSetMethod());
                break;
            }
        }
    }

    private List<XmlObject> getItems(XmlReader reader, XmlObject xmlObject, AnnotatedElement field, String name) {
        List<XmlObject> xmlObjects;
        if (reader.getOptions().isSameAsList()) {
//            System.out.println(name+"  -> "+xmlObject);
            xmlObjects = xmlObject.getElementList(name);
        } else {
            String subtag = reader.getItemTagName(field);
            XmlObject subObject = xmlObject.getElement(name);
            if (subObject != null) {
                xmlObjects = subObject.getElementList(subtag);
            } else {
                xmlObjects = null;
            }
        }
        return xmlObjects;
    }

    @SuppressWarnings("unchecked")
    private <T> T array(XmlReader reader, List<XmlObject> xmlObjects, Class<T> pClass, Object object)
            throws Exception {
        if (xmlObjects == null) {
            return null;
        }
        Class<?> sc = pClass.getComponentType();
        int count = xmlObjects.size();
        T t;
        if (object != null) {
            t = (T) object;
        } else {
            t = (T) Array.newInstance(sc, count);
        }
        for (int i = 0; i < count; i++) {
            XmlObject xmlObject = xmlObjects.get(i);
            xmlObject.setType(sc);
            XmlTypeAdapter adapter = reader.getAdapter(sc);
            Object o = adapter.read(reader, null, xmlObject, null);
            if (o != null) {
                Array.set(t, i, o);
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T map(XmlReader reader, List<XmlObject> xmlObjects, Class<T> pClass, Object object, Class<?>[] subClass)
            throws Exception {
        if (xmlObjects == null || subClass == null || subClass.length < 2) {
            return null;
        }
        T t;
        if (object == null) {
            t = Reflect.create(pClass, subClass);
            if (reader.DEBUG)
                Log.v("xml", "create map " + pClass.getName());
        } else {
            t = (T) object;
        }
        if (t == null) return t;
        if (reader.DEBUG)
            Log.v("xml", " put " + subClass[0] + "," + subClass[1] + " size=" + xmlObjects.size());
        for (XmlObject xmlObject : xmlObjects) {
            Class<?> kc = subClass[0];
            XmlObject tk = xmlObject.get(XmlElementMap.KEY);
            tk.setType(kc);
            XmlTypeAdapter adapterk = reader.getAdapter(kc);
            Object k = adapterk.read(reader, xmlObject, tk, null);
            Class<?> vc = subClass[1];
            XmlObject tv = xmlObject.get(XmlElementMap.VALUE);
            tv.setType(vc);
            XmlTypeAdapter adapterv = reader.getAdapter(vc);
            Object v = adapterv.read(reader, xmlObject, tv, null);
            if (reader.DEBUG) {
                Log.v("xml", xmlObject.getName() + " put " + (tk != null) + "=" + (tv != null));
                Log.v("xml", xmlObject.getName() + " put " + k + "=" + v);
            }
            if (k != null)
                Reflect.call(t.getClass(), t, "put", k, v);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T list(XmlReader reader, List<XmlObject> xmlObjects, Class<T> pClass, Object object, Class<?> subClass)
            throws Exception {
        if (xmlObjects == null) {
            return null;
        }
        if (reader.DEBUG)
            Log.v("xml", "list " + subClass.getName());
        T t;
        if (object == null) {
            t = Reflect.create(pClass, subClass);
            if (reader.DEBUG)
                Log.v("xml", "create list " + pClass.getName());
        } else {
            t = (T) object;
        }
        if (t != null) {
            //多种派生类
//            boolean d = XmlClassSearcher.class.isAssignableFrom(subClass);
            for (XmlObject xmlObject : xmlObjects) {
                Class<?> sc = subClass;
                xmlObject.setType(sc);
                XmlTypeAdapter adapter = reader.getAdapter(sc);
                Object sub = adapter.read(reader, null, xmlObject, null);
                if (sub != null)
                    Reflect.call(t.getClass(), t, "add", sub);
                else {
                    if (reader.DEBUG)
                        Log.w("xml", xmlObject.getName() + "@" + sc.getName() + " is null");
                }
            }
        }
        return t;
    }

}