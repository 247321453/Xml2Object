package org.xml.core;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * {@link Element } 转文件
 */
public class XmlWriter {
    protected XmlConvert mXmlConvert;

    public XmlWriter() {
        mXmlConvert = new XmlConvert();
    }

    /***
     * @param object       java对象
     * @param outputStream 输出流
     * @param encoding     xml编码
     * @throws IOException            io异常
     * @throws IllegalAccessException 类型异常
     */
    public void toXml(Object object, OutputStream outputStream, String encoding)
            throws IOException, IllegalAccessException {
        toXml(mXmlConvert.toTag(object, null), outputStream, encoding);
    }

    private void toXml(Element element, OutputStream outputStream, String encoding)
            throws IOException {
        if (outputStream == null) return;
        if (IXml.DEBUG)
            Log.v("xml", "to " + element);
        XmlSerializer serializer = Xml.newSerializer();
        if (encoding == null) {
            encoding = IXml.DEF_ENCODING;
        }
        serializer.setOutput(outputStream, encoding);
        serializer.startDocument(encoding, null);
        writeTag(element, serializer);
        serializer.endDocument();
    }

    @SuppressWarnings("unchecked")
    private void writeTag(Element element, XmlSerializer serializer)
            throws IOException {
        if (element == null || serializer == null) return;
        serializer.startTag(null, element.getName());
        for (Map.Entry<String, String> e : element.getAttributes().entrySet()) {
            serializer.attribute(null, e.getKey(), e.getValue());
        }
        serializer.text(element.getText());
        int count = element.size();
        for (int i = 0; i < count; i++) {
            writeTag(element.get(i), serializer);
        }
        serializer.endTag(null, element.getName());
    }
}
