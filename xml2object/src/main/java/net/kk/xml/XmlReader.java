package net.kk.xml;

import android.util.Log;

import net.kk.xml.annotations.XmlElementList;
import net.kk.xml.annotations.XmlElementMap;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

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
    public <T> T from(InputStream inputStream, Class<T> pClass, String encoding)
            throws Exception {
        XmlObject tag = mXmlPullReader.toTag(pClass, inputStream, encoding);
        if (DEBUG)
            Log.d("xml", "form " + tag);
        return (T) mXmlObjectReader.read(null, tag, null);
    }

    /***
     * @param xmlStr 输入文字
     * @param pClass 类
     * @param <T>    类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <T> T from(String xmlStr, Class<T> pClass)
            throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlStr.getBytes());
        XmlObject tag = mXmlPullReader.toTag(pClass, inputStream, null);
        inputStream.close();
        if (DEBUG)
            Log.d("xml", "form " + tag);
        return (T) mXmlObjectReader.read(null, tag, null);
    }

    public Class<?> getListClass(AnnotatedElement cls) {
        if (cls == null) return Object.class;
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
