package net.kk.xml;

import net.kk.xml.annotations.XmlElementList;
import net.kk.xml.annotations.XmlElementMap;
import net.kk.xml.internal.Reflect;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

/***
 * {@link XmlObject } 转对象
 * 支持enum，enum的值为名字（混淆前)
 */
public class XmlReader extends XmlBase {
    private XmlPullReader mXmlPullReader;
    private XmlObjectReader mXmlObjectReader;

    public XmlReader(XmlPullParser xmlParser, XmlOptions options) {
        super(options);
        this.mXmlObjectReader = new XmlObjectReader(this);
        mXmlPullReader = new XmlPullReader(this, xmlParser);
    }

    /***
     * @param inputStream 输入流
     * @param pClass      类
     * @param <T>         类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <T> T from(InputStream inputStream, Class<T> pClass, String encoding) throws Exception {
        XmlObject tag = toXmlObject(inputStream, pClass, encoding);
        if (DEBUG) {
            System.out.println("form " + tag);
        }
        return (T) mXmlObjectReader.read(null, tag, null, null);
    }

    /***
     * xml转为xmlobject
     *
     * @param inputStream 流
     * @param pClass      类
     * @param encoding    编码
     */
    public <T> XmlObject toXmlObject(InputStream inputStream, Class<T> pClass, String encoding) {
        return mXmlPullReader.toTag(pClass, inputStream, encoding);
    }

    /***
     * xml转为xmlobject
     *
     * @param xmlStr   xml
     * @param pClass   类
     * @param encoding 编码
     */
    public <T> XmlObject toXmlObject(String xmlStr, Class<T> pClass, String encoding) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlStr.getBytes());
        XmlObject xmlObject = null;
        try {
            xmlObject = mXmlPullReader.toTag(pClass, inputStream, encoding);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        return xmlObject;
    }

    /***
     * @param xmlStr 输入文字
     * @param pClass 类
     * @param <T>    类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <T> T from(String xmlStr, Class<T> pClass) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlStr.getBytes());
        return from(inputStream, pClass, null);
    }

    public Class<?> getListClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
        if (cls instanceof Field) {
            return Reflect.getListClass((Field) cls);
        }
        XmlElementList xmlElement = cls.getAnnotation(XmlElementList.class);
        if (xmlElement != null) {
            if (xmlElement.type() != null) {
                return xmlElement.type();
            }
        }
        return Object.class;
    }

    public Class<?>[] getMapClass(AnnotatedElement cls) {
        if (cls == null)
            return new Class[]{Object.class, Object.class};
        if (cls instanceof Field) {
           return Reflect.getMapClass((Field) cls);
        }
        XmlElementMap xmlElement = cls.getAnnotation(XmlElementMap.class);
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

    public Field getTagFiled(Class<?> type, String name) {
        // 尝试作为公有字段处理
        do {
            Field[] fields = type.getDeclaredFields();
            for (Field f : fields) {
                String xmltag = getTagName(f);
                if (name.equals(xmltag)) {
                    return f;
                }
            }
            type = type.getSuperclass();
        } while (type != null);
        return null;
    }
}
