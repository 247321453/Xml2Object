package org.xml.core;

import android.util.Log;
import android.util.Xml;

import org.xml.convert.TypeToken;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * {@link TypeToken } 转文件
 */
public class XmlWriter {
    protected XmlConvert mXmlConvert;
    private static final boolean SPACE = false;
    private static final String NEW_LINE = System.getProperty("line.separator", "\n");

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
        toXml(mXmlConvert.toTag(object), outputStream, encoding);
    }

    private void toXml(TypeToken typeToken, OutputStream outputStream, String encoding)
            throws IOException {
        if (outputStream == null) return;
        if (KXml.DEBUG)
            Log.d("xml", "to " + typeToken);
        XmlSerializer serializer = Xml.newSerializer();
        if (encoding == null) {
            encoding = KXml.DEF_ENCODING;
        }
        serializer.setOutput(outputStream, encoding);
        serializer.startDocument(encoding, null);
        if (SPACE)
            serializer.text(NEW_LINE);
        writeTag(typeToken, serializer, 1);
        serializer.endDocument();
    }

    @SuppressWarnings("unchecked")
    private void writeTag(TypeToken typeToken, XmlSerializer serializer, int depth)
            throws IOException {
        if (typeToken == null || serializer == null) return;
        if (SPACE) {
            serializer.text(NEW_LINE);
            writeTab(serializer, depth);
        }
        serializer.startTag(null, typeToken.getName());
        for (Map.Entry<String, String> e : typeToken.getAttributes().entrySet()) {
            serializer.attribute(null, e.getKey(), e.getValue());
        }
        serializer.text(typeToken.getText() == null ? "" : typeToken.getText());
        int count = typeToken.size();
        for (int i = 0; i < count; i++) {
            writeTag(typeToken.get(i), serializer, depth + 1);
        }
        serializer.endTag(null, typeToken.getName());
    }

    private void writeTab(XmlSerializer pXmlSerializer, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            pXmlSerializer.text("\t");
        }
    }
}
