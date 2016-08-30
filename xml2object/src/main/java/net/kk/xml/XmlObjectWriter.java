package net.kk.xml;

import android.util.Log;

import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.internal.Reflect;
import net.kk.xml.internal.XmlStringAdapter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

class XmlObjectWriter {
    XmlWriter writer;
    XmlOptions options;

    public XmlObjectWriter(XmlWriter writer) {
        this.writer = writer;
        this.options = writer.getOptions();
    }

    @SuppressWarnings("unchecked")
    public XmlObject toObject(String name, AnnotatedElement element, Object object) throws Exception {
        if (name == null) {
            name = writer.getTagName(element);
        }
        if (name == null || element == null || object == null) return null;
        XmlObject root = new XmlObject(name);
        Class<?> cls = object.getClass();
        root.setType(cls);
        root.setNamespace(writer.getNamespace(element));
        if (writer.isNormal(element)) {
            XmlStringAdapter stringAdapter = writer.getAdapter(cls);
            root.setText(stringAdapter.toString(cls, object));
        } else if (cls.isArray()) {
            root.addAllChilds(array(object, element));
        } else if (object instanceof Map) {
            root.addAllChilds(map(object, cls, element));
        } else if (object instanceof Collection) {
            root.addAllChilds(list(object, element));
        } else {
            writeText(object, root);
            writeAttributes(object, root);
            writeSubTag(object, root);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<XmlObject> map(Object object, Class<?> pClass, AnnotatedElement element) throws Exception {
        String name = options.isSameAsList() ? writer.getTagName(element) : writer.getItemTagName(element);
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
                Class<?> kcls = k.getClass();
                XmlObject xmlkey = toObject(XmlElementMap.KEY, kcls, k);
                xmlkey.setNamespace(writer.getNamespace(element));
                xmlObject.addChild(xmlkey);
                if (v == null) {
                    xmlObject.addChild(new XmlObject(XmlElementMap.VALUE));
                } else {
                    Class<?> cls = v.getClass();
                    XmlObject xmlval = toObject(XmlElementMap.VALUE, cls, v);
                    xmlval.setNamespace(writer.getNamespace(element));
                    xmlObject.addChild(xmlval);
                }
                list.add(xmlObject);
            }
        }
        return list;
    }

    private List<XmlObject> array(Object object, AnnotatedElement element) throws Exception {
        String name = options.isSameAsList() ? writer.getTagName(element) : writer.getItemTagName(element);
        ArrayList<XmlObject> list = new ArrayList<XmlObject>();
        if (object != null) {
            int count = Array.getLength(object);
            for (int i = 0; i < count; i++) {
                Object obj = Array.get(object, i);
                if (obj != null) {
                    Class<?> cls = obj.getClass();
                    //
                    XmlObject object1 = toObject(name, cls, obj);
                    object1.setNamespace(writer.getNamespace(element));
                    object1.setSubItem(true);
                    list.add(object1);
                }
            }
        }
        return list;
    }

    private List<XmlObject> list(Object object, AnnotatedElement element) throws Exception {
        String name = options.isSameAsList() ? writer.getTagName(element) : writer.getItemTagName(element);
        ArrayList<XmlObject> list = new ArrayList<XmlObject>();
        if (object != null) {
            Object[] objs = (Object[]) Reflect.call(object.getClass(), object, "toArray");
            if (objs != null) {
                for (Object obj : objs) {
                    if (obj != null) {
                        Class<?> cls = obj.getClass();
                        XmlObject object1 = toObject(name, cls, obj);
                        object1.setNamespace(writer.getNamespace(element));
                        object1.setSubItem(true);
                        list.add(object1);
                    }
                }
            }
        }
        return list;
    }

    private void writeText(Object object, XmlObject xmlObject) throws IllegalAccessException {
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

    @SuppressWarnings("unchecked")
    private void writeAttributes(Object object, XmlObject parent) throws IllegalAccessException {
        if (object == null || parent == null) return;
        Class<?> cls = object.getClass();
        Collection<Field> fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            String subTag = writer.getAttributeName(field);
            if (subTag == null)
                continue;
            Class<?> type = field.getType();
            Reflect.accessible(field);
            Object val = field.get(object);
            XmlStringAdapter xmlStringAdapter = writer.getAdapter(type);
            parent.addAttribute(writer.getNamespace(field), subTag, xmlStringAdapter.toString(type, val));
        }
    }

    private void writeSubTag(Object object, XmlObject parent) throws Exception {
        if (object == null) return;
        //内部类
        boolean isInner = object.getClass().isMemberClass();
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (isIgnore(field)) {
                continue;
            }
            if(options.isIgnoreStatic() && (field.getModifiers()& Modifier.STATIC) !=0){
                continue;
            }
            String name = writer.getTagName(field);
            String fname = field.getName();
            if (name == null)
                continue;
            if (isInner) {
                if ("this$0".equals(fname)) {
                    continue;
                }
            }
            Reflect.accessible(field);
            Object val = field.get(object);
            XmlObject fobject = toObject(name, field, val);
            if (fobject != null) {
                fobject.setType(field);
                parent.addChild(fobject);
            }
        }
    }

    protected boolean isIgnore(Field field) {
        Class<?> type = Reflect.wrapper(field.getType());
        if (type.isArray()) {
            return options.isIgnore(type.getComponentType());
        } else if (List.class.isAssignableFrom(type)) {
            return options.isIgnore(writer.getListClass(field));
        } else if (Map.class.isAssignableFrom(type)) {
            Class<?>[] classes = writer.getMapClass(field);
            return options.isIgnore(classes[0]) || options.isIgnore(classes[1]);
        } else {
            return options.isIgnore(type);
        }
    }
}
