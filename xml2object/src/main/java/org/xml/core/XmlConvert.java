package org.xml.core;

import android.util.Log;

import org.xml.annotation.XmlElementMap;
import org.xml.convert.Reflect;
import org.xml.convert.TypeToken;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class XmlConvert{
    //region to tag

//    final Map<Class<?>, XmlClassSearcher> mXmlClassSearcherMap = new HashMap<>();

//    /**
//     * @param tClass  XmlClassSearcher的派生类
//     * @param element xml元素
//     * @return 类型
//     * @throws IllegalAccessException    异常1
//     * @throws InstantiationException    异常2
//     * @throws InvocationTargetException 异常3
//     */
//    public Class<?> getSubClass(Class<?> tClass, Element element)
//            throws IllegalAccessException, InstantiationException, InvocationTargetException {
//        XmlClassSearcher searcher = mXmlClassSearcherMap.get(tClass);
//        if (searcher == null) {
//            searcher = (XmlClassSearcher) Reflect.create(tClass);
//            mXmlClassSearcherMap.put(tClass, searcher);
//        }
//        if (searcher == null) return Object.class;
//        return searcher.getSubClass(element.getTagNames());
//    }

    /**
     * 从流转换为tag对象
     *
     * @param tClass      解析的类
     * @param inputStream 输入流
     * @return tag对象
     */
    public TypeToken toTag(Class<?> tClass, InputStream inputStream, String encoding) {
        if (inputStream == null) return null;
        XmlPullParser xmlParser = android.util.Xml.newPullParser();
        Map<Integer, TypeToken> tagMap = new HashMap<>();
        int depth = -1;
        String name = KXml.getTagName(tClass);
        if (name == null) {
            name = tClass.getSimpleName();
        }
        TypeToken mTypeToken = new TypeToken(name);
        mTypeToken.setType(tClass);
        tagMap.put(1, mTypeToken);
        TypeToken parent = null;
        String xmlTag = null;
        try {
            xmlParser.setInput(inputStream, encoding == null ? KXml.DEF_ENCODING : encoding);
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                // 一直循环，直到文档结束
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        //属性
                        xmlTag = xmlParser.getName();
                        int d = xmlParser.getDepth();
                        if (depth < 0) {
                            //
                        } else {
                            parent = tagMap.get(d - 1);
                            mTypeToken = new TypeToken(xmlTag);
                            mTypeToken.setType(findTagClass(parent, xmlTag));
                            if (KXml.DEBUG)
                                Log.v("xml", xmlTag + "@" + mTypeToken.getTClass().getName());
                            if (parent != null) {
                                parent.add(mTypeToken);
                                if (KXml.DEBUG)
                                    Log.v("xml", parent.getName() + " add " + mTypeToken.getName());
                            } else {
                            }
                            tagMap.put(d, mTypeToken);
                        }
                        depth = d;
                        int count = xmlParser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String k = xmlParser.getAttributeName(i);
                            String v = xmlParser.getAttributeValue(i);
                            mTypeToken.addAttribute(k, v);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = xmlParser.getText();
                        if (KXml.DEBUG)
                            Log.d("xml", xmlTag + " text = " + text);
                        if (mTypeToken.getText() == null)
                            mTypeToken.setText(text);
                        break;
                    case XmlPullParser.END_TAG:
                        // mElement.setType(findTagClass(parent, xmlTag, mElement));
                        break;
                }
                // 如果xml没有结束，则导航到下一个river节点
                evtType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return tagMap.get(1);
    }

    //ednregion

//    private void updateClass(Element pElement)
//            throws IllegalAccessException, InvocationTargetException, InstantiationException {
//        if (pElement == null) return;
//        AnnotatedElement type = pElement.getType();
//        String name = pElement.getName();
//        Class<?> pClass = null;
//        if (pElement.isArray()) {
//            pClass = getSubClass(getArrayClass(type), pElement);
//        } else if (pElement.isMap()) {
//            if (MAP_KEY.equals(name)) {
//                pClass = getSubClass(getMapClass(type)[0], pElement);
//            } else {
//                pClass = getSubClass(getMapClass(type)[1], pElement);
//            }
//        } else if (pElement.isList()) {
//            pClass = getSubClass(getListClass(type), pElement);
//        }
//        if (pClass != null)
//            pElement.updateTClass(pClass);
//    }

    private AnnotatedElement findTagClass(TypeToken p, String name)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (p == null || name == null) {
            return Object.class;
        }
        Class<?> pClass = p.getTClass();
        AnnotatedElement type = p.getType();
        XmlElementMap xmlElementMap = type.getAnnotation(XmlElementMap.class);
        String keyName;
        String valueName;
        if (xmlElementMap == null) {
            keyName = KXml.MAP_KEY_NAME;
            valueName = KXml.MAP_VALUE_NAME;
        } else {
            keyName = xmlElementMap.keyName();
            valueName = xmlElementMap.valueName();
        }

        if (keyName.equals(name)) {
            return KXml.getMapClass(type)[0];
        }
        if (valueName.equals(name)) {
            return KXml.getMapClass(type)[1];
        }
        if (pClass.isArray()) {
            pClass = pClass.getComponentType();
            if (KXml.DEBUG)
                Log.d("xml", name + " is " + pClass.getName());
        }
        if (Collection.class.isAssignableFrom(pClass)) {
            pClass = KXml.getListClass(p.getType());
        }
        Collection<Field> fields = Reflect.getFileds(pClass);
        Field tfield = null;
        for (Field field : fields) {
            String tagname = KXml.getTagName(field);
            if (name.equals(tagname)) {
                tfield = field;
                break;
            }
        }
        if (tfield == null) {
            if (KXml.DEBUG)
                Log.w("xml", "no find " + name + " form " + pClass.getName());
            return Object.class;
        }
        return tfield;
    }


    /**
     * 从java对象转换为tag对象
     *
     * @param object java对象
     * @return tag对象
     */
    public TypeToken toTag(Object object) throws IllegalAccessException {
        if (object == null) return new TypeToken("null");
        Class<?> pClass = object.getClass();
        String name = KXml.getTagName(pClass);
        TypeToken root = new TypeToken(name);
        root.setType(pClass);
        writeAttributes(object, root);
        writeText(object, root);
        writeSubTag(object, root);
        return root;
    }

    private TypeToken any(Object object, Class<?> pClass, String name)
            throws IllegalAccessException {
        TypeToken typeToken = new TypeToken(name);
        if (pClass == null) {
            if (object == null) {
                return typeToken;
            }
            pClass = object.getClass();
        }
        typeToken.setType(pClass);
        if (name == null) {
            name = KXml.getTagName(pClass);
            typeToken.setName(name);
        }
        if (Reflect.isNormal(pClass)) {
            typeToken.setText(KXml.toString(object));
        } else {
            writeAttributes(object, typeToken);
            writeText(object, typeToken);
            writeSubTag(object, typeToken);
        }
        return typeToken;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<TypeToken> map(Object object,AnnotatedElement type, String name) throws IllegalAccessException {
        ArrayList<TypeToken> list = new ArrayList<>();
        if (object == null) {
            return list;
        }
        String keyName;
        String valueName;
        XmlElementMap xmlElementMap = type.getAnnotation(XmlElementMap.class);
        if (xmlElementMap == null) {
            keyName = KXml.MAP_KEY_NAME;
            valueName = KXml.MAP_VALUE_NAME;
        } else {
            keyName = xmlElementMap.keyName();
            valueName = xmlElementMap.valueName();
        }
        Object set = Reflect.call(object.getClass(), object, "entrySet");
        if (set instanceof Set) {
            Set<Map.Entry<?, ?>> sets = (Set<Map.Entry<?, ?>>) set;
            for (Map.Entry<?, ?> e : sets) {
                if (KXml.DEBUG)
                    Log.v("xml", "map " + e);
                Object k = e.getKey();
                if (k == null) {
                    continue;
                }
                Object v = e.getValue();
                TypeToken typeToken = new TypeToken(name);
                typeToken.setType(type);
                typeToken.add(any(k, k.getClass(), keyName));
                typeToken.add(any(v, v == null ? null : v.getClass(), valueName));
                list.add(typeToken);
            }
        }
        return list;
    }

    private ArrayList<TypeToken> array(Object object, String name) throws IllegalAccessException {
        ArrayList<TypeToken> list = new ArrayList<>();
        if (object != null) {
            int count = Array.getLength(object);
            for (int i = 0; i < count; i++) {
                Object obj = Array.get(object, i);
                if (obj != null) {
                    list.add(any(obj, obj.getClass(), name));
                }
            }
        }
        return list;
    }

    private ArrayList<TypeToken> list(Object object, String name) throws IllegalAccessException {
        ArrayList<TypeToken> list = new ArrayList<>();
        if (object != null) {
            Object[] objs = (Object[]) Reflect.call(object.getClass(), object, "toArray");
            if (objs != null) {
                for (Object o : objs) {
                    if (o != null)
                        list.add(any(o, o.getClass(), name));
                }
            }
        }
        return list;
    }

    //region write
    private void writeAttributes(Object object, TypeToken parent) throws IllegalAccessException {
        if (object == null || parent == null) return;
        Class<?> cls = object.getClass();
        Collection<Field> fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            String subTag = KXml.getAttributeName(field);
            if (subTag == null)
                continue;
            Reflect.accessible(field);
            Object val = field.get(object);
            if (KXml.DEBUG)
                Log.v("xml", subTag + "=" + val);
            parent.addAttribute(subTag, KXml.toString(val));
        }
    }

    private void writeSubTag(Object object, TypeToken parent) throws IllegalAccessException {
        if (object == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            String name = KXml.getTagName(field);
            if (name == null)
                continue;
            Reflect.accessible(field);
            Object val = field.get(object);
            if (Reflect.isNormal(field.getType())) {
                TypeToken typeToken = new TypeToken(name);
                typeToken.setType(field.getType());
                typeToken.setText(KXml.toString(val));
                parent.add(typeToken);
            } else if (field.getType().isArray()) {
                if (KXml.DEBUG)
                    Log.d("xml", parent.getName() + " add array " + field.getName());
                parent.addAll(array(val, name));
            } else if (val instanceof Map) {
                if (KXml.DEBUG)
                    Log.d("xml", parent.getName() + " add map " + field.getName());
                parent.addAll(map(val, field, name));
            } else if (val instanceof Collection) {
                if (KXml.DEBUG)
                    Log.d("xml", parent.getName() + " add list " + field.getName());
                parent.addAll(list(val, name));
            } else if (val != null) {
                if (KXml.DEBUG)
                    Log.d("xml", parent.getName() + " add any " + field.getName());
                parent.add(any(val, field.getType(), name));
            }
        }
    }

    private void writeText(Object object, TypeToken typeToken) throws IllegalAccessException {
        if (object == null || typeToken == null) return;
        Class<?> cls = object.getClass();
        Collection<Field> fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if (KXml.isXmlValue(field)) {
                Reflect.accessible(field);
                Object val = field.get(object);
                typeToken.setText(KXml.toString(val));
                break;
            }
        }
    }
    //endregion
}
