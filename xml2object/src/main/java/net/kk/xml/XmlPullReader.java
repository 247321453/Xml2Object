package net.kk.xml;

import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.internal.Reflect;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class XmlPullReader {
    private XmlPullParser xmlParser;
    private XmlReader mXmlReader;

    public XmlPullReader(XmlReader reader, XmlPullParser xmlParser) {
        this.mXmlReader = reader;
        this.xmlParser = xmlParser;
    }

    /**
     * 从流转换为tag对象
     *
     * @param tClass      解析的类
     * @param inputStream 输入流
     * @return tag对象
     */
    public XmlObject toTag(Class<?> tClass, InputStream inputStream, String encoding) {
        Map<Integer, XmlObject> tagMap = new HashMap<Integer, XmlObject>();
        int depth = -1;
        String name = mXmlReader.getTagName(tClass);
        if (name == null) {
            name = tClass.getSimpleName();
        }
        XmlObject mXmlObject = new XmlObject(name);
        mXmlObject.setType(tClass);
        tagMap.put(1, mXmlObject);
        XmlObject parent = null;
        String xmlTag = null;
        try {
            if (inputStream != null) {
                xmlParser.setInput(inputStream, encoding == null ? XmlReader.DEF_ENCODING : encoding);
            }
            int evtType = xmlParser.getEventType();
            int _index = 0;
            while (evtType != XmlPullParser.END_DOCUMENT) {
                // 一直循环，直到文档结束
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        // 属性
                        xmlTag = xmlParser.getName();
                        int d = xmlParser.getDepth();
                        if (depth < 0) {
                            //
                        } else {
                            parent = tagMap.get(d - 1);
                            mXmlObject = new XmlObject(xmlTag);
                            mXmlObject.setType(findTagClass(parent, xmlTag));
                            if (parent != null) {
                                mXmlObject.setIndex(_index++);
                                parent.addChild(mXmlObject);
                            }else{
                                _index = 0;
                            }
                            tagMap.put(d, mXmlObject);
                        }
                        depth = d;
                        int count = xmlParser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String np = xmlParser.getAttributeNamespace(i);
                            String k = xmlParser.getAttributeName(i);
                            String v = xmlParser.getAttributeValue(i);
                            mXmlObject.addAttribute(np, k, v);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = xmlParser.getText();
                        if (mXmlObject.getText() == null) {
                            if (text != null) {
                                text = text.trim();
                            }
                            mXmlObject.setText(text);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        // mElement.setType(findTagClass(parent, xmlTag, mElement));
                        break;
                }
                evtType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return tagMap.get(1);
    }

    private AnnotatedElement findTagClass(XmlObject p, String name)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (p == null || name == null) {
            return Object.class;
        }
        Class<?> pClass = p.getTClass();

        if (XmlElementMap.KEY.equals(name)) {
            return mXmlReader.getMapClass(p.getType())[0];
        }
        if (XmlElementMap.VALUE.equals(name)) {
            return mXmlReader.getMapClass(p.getType())[1];
        }
        if (pClass.isArray()) {
            pClass = pClass.getComponentType();
            // if (IXml.DEBUG)
            // Log.d("xml", name + " is " + pClass.getName());
        }
        if (Collection.class.isAssignableFrom(pClass)) {
            pClass = mXmlReader.getListClass(p.getType());
        }
        Collection<Field> fields = Reflect.getFileds(pClass);
        Field tfield = null;
        for (Field field : fields) {
            String tagname = mXmlReader.getTagName(field);
            if(mXmlReader.getOptions().isIgnoreTagCase()){
                if (name.equalsIgnoreCase(tagname)) {
                    tfield = field;
                    break;
                }
            }else {
                if (name.equals(tagname)) {
                    tfield = field;
                    break;
                }
            }
        }
        if (tfield == null) {
            // if (IXml.DEBUG)
            // Log.w("xml", "no find " + name + " form " + pClass.getName());
            return Object.class;
        }
        return tfield;
    }

}
