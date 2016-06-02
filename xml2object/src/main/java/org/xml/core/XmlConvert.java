package org.xml.core;

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

class XmlConvert extends IXml {
    XmlPullParser xmlParser;
    /***
     * true
     * <pre>
     *     list
     *     list
     *
     *     list
     *     list
     * </pre>
     * <pre>
     * lists
     *     list
     *     list
     * lists
     * </pre>
     */
    private boolean sameAsList = true;

    public XmlConvert(XmlPullParser xmlParser) {
        this.xmlParser = xmlParser;
    }
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

    public void setSameAsList(boolean sameAsList) {
        this.sameAsList = sameAsList;
    }

    public boolean isSameAsList() {
        return sameAsList;
    }

    /**
     * 从流转换为tag对象
     *
     * @param tClass      解析的类
     * @param inputStream 输入流
     * @return tag对象
     */
    public Element toTag(Class<?> tClass, InputStream inputStream, String encoding) {
        if (inputStream == null) return null;
        Map<Integer, Element> tagMap = new HashMap<Integer, Element>();
        int depth = -1;
        String name = getTagName(tClass);
        if (name == null) {
            name = tClass.getSimpleName();
        }
        Element mElement = new Element(name);
        mElement.setType(tClass);
        tagMap.put(1, mElement);
        Element parent = null;
        String xmlTag = null;
        try {
            xmlParser.setInput(inputStream, encoding == null ? DEF_ENCODING : encoding);
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
                            mElement = new Element(xmlTag);
                            mElement.setType(findTagClass(parent, xmlTag));
//                            if (IXml.DEBUG)
//                                Log.v("xml", xmlTag + "@" + mElement.getTClass().getName());
                            if (parent != null) {
                                parent.add(mElement);
//                                if (IXml.DEBUG)
//                                    Log.v("xml", parent.getName() + " add " + mElement.getName());
//                            } else {
                            }
                            tagMap.put(d, mElement);
                        }
                        depth = d;
                        int count = xmlParser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String k = xmlParser.getAttributeName(i);
                            String v = xmlParser.getAttributeValue(i);
                            mElement.addAttribute(k, v);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = xmlParser.getText();
//                        if (IXml.DEBUG)
//                            Log.d("xml", xmlTag + " text = " + text);
                        if (mElement.getText() == null) {
                            if (text != null) {
                                text = text.trim();
                            }
                            mElement.setText(text);
                        }
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

    private AnnotatedElement findTagClass(Element p, String name)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (p == null || name == null) {
            return Object.class;
        }
        Class<?> pClass = p.getTClass();

        if (MAP_KEY.equals(name)) {
            return getMapClass(p.getType())[0];
        }
        if (MAP_VALUE.equals(name)) {
            return getMapClass(p.getType())[1];
        }
        if (pClass.isArray()) {
            pClass = pClass.getComponentType();
//            if (IXml.DEBUG)
//                Log.d("xml", name + " is " + pClass.getName());
        }
        if (Collection.class.isAssignableFrom(pClass)) {
            pClass = getListClass(p.getType());
        }
        Collection<Field> fields = Reflect.getFileds(pClass);
        Field tfield = null;
        for (Field field : fields) {
            String tagname = getTagName(field);
            if (name.equals(tagname)) {
                tfield = field;
                break;
            }
        }
        if (tfield == null) {
//            if (IXml.DEBUG)
//                Log.w("xml", "no find " + name + " form " + pClass.getName());
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
    public Element toTag(Object object) throws IllegalAccessException {
        if (object == null) return new Element("null");
        Class<?> pClass = object.getClass();
        String name = getTagName(pClass);
        Element root = new Element(name);
        root.setType(pClass);
        writeAttributes(object, root);
        writeText(object, root);
        writeSubTag(object, root);
        return root;
    }

    private Element any(Object object, Class<?> pClass, String name)
            throws IllegalAccessException {
        Element element = new Element(name);
        if (pClass == null) {
            if (object == null) {
                return element;
            }
            pClass = object.getClass();
        }
        element.setType(pClass);
        if (name == null) {
            name = getTagName(pClass);
            element.setName(name);
        }
        if (Reflect.isNormal(pClass)) {
            element.setText(toString(object));
        } else {
            writeAttributes(object, element);
            writeText(object, element);
            writeSubTag(object, element);
        }
        return element;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Element> map(Object object, Class<?> pClass, String name) throws IllegalAccessException {
        ArrayList<Element> list = new ArrayList<Element>();
        if (object == null) {
            return list;
        }
        Object set = Reflect.call(object.getClass(), object, "entrySet");
        if (set instanceof Set) {
            Set<Map.Entry<?, ?>> sets = (Set<Map.Entry<?, ?>>) set;
            for (Map.Entry<?, ?> e : sets) {
//                if (IXml.DEBUG)
//                    Log.v("xml", "map " + e);
                Object k = e.getKey();
                if (k == null) {
                    continue;
                }
                Object v = e.getValue();
                Element element = new Element(name);
                element.setType(pClass);
                element.add(any(k, k.getClass(), MAP_KEY));
                element.add(any(v, v == null ? null : v.getClass(), MAP_VALUE));
                list.add(element);
            }
        }
        return list;
    }

    private ArrayList<Element> array(Object object, String name) throws IllegalAccessException {
        ArrayList<Element> list = new ArrayList<Element>();
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

    private ArrayList<Element> list(Object object, String name) throws IllegalAccessException {
        ArrayList<Element> list = new ArrayList<Element>();
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
    private void writeAttributes(Object object, Element parent) throws IllegalAccessException {
        if (object == null || parent == null) return;
        Class<?> cls = object.getClass();
        Collection<Field> fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            String subTag = getAttributeName(field);
            if (subTag == null)
                continue;
            Reflect.accessible(field);
            Object val = field.get(object);
//            if (IXml.DEBUG)
//                Log.v("xml", subTag + "=" + val);
            parent.addAttribute(subTag, toString(val));
        }
    }

    private void writeSubTag(Object object, Element parent) throws IllegalAccessException {
        if (object == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            String name = getTagName(field);
            if (name == null)
                continue;
            Class<?> cls = field.getType();
            Reflect.accessible(field);
            Object val = field.get(object);
            if (Reflect.isNormal(cls)) {
                Element element = new Element(name);
                element.setType(cls);
                element.setText(toString(val));
                parent.add(element);
            } else if (cls.isArray()) {
//                if (IXml.DEBUG)
//                    Log.d("xml", parent.getName() + " add array " + field.getName());
                if(isSameAsList()){
                    parent.addAll(array(val, name));
                }else{
                    String subtag = getItemTagName(field);
                    Element element=new Element(name);
                    element.addAll(array(val, subtag));
                    parent.add(element);
                }
            } else if (val instanceof Map) {
//                if (IXml.DEBUG)
//                    Log.d("xml", parent.getName() + " add map " + field.getName());
                if(isSameAsList()){
                    parent.addAll(map(val, cls, name));
                }else{
                    String subtag = getItemTagName(field);
                    Element element=new Element(name);
                    element.addAll(map(val, cls, subtag));
                    parent.add(element);
                }
            } else if (val instanceof Collection) {
//                if (IXml.DEBUG)
//                    Log.d("xml", parent.getName() + " add list " + field.getName());
                if(isSameAsList()){
                    parent.addAll(list(val, name));
                }else{
                    String subtag = getItemTagName(field);
                    Element element=new Element(name);
                    element.addAll(list(val, subtag));
                    parent.add(element);
                }
            } else if (val != null) {
//                if (IXml.DEBUG)
//                    Log.d("xml", parent.getName() + " add any " + field.getName());
                parent.add(any(val, cls, name));
            }
        }
    }

    private void writeText(Object object, Element element) throws IllegalAccessException {
        if (object == null || element == null) return;
        Class<?> cls = object.getClass();
        Collection<Field> fields = Reflect.getFileds(cls);
        for (Field field : fields) {
            if (isXmlValue(field)) {
                Reflect.accessible(field);
                Object val = field.get(object);
                element.setText(toString(val));
                break;
            }
        }
    }
    //endregion
}
