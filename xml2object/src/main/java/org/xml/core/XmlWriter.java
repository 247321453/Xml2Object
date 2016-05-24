package org.xml.core;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * {@link Element } 转文件
 */
public class XmlWriter {
    protected XmlConvert mXmlConvert;
    private static final boolean SPACE = false;
    private static final String NEW_LINE = System.getProperty("line.separator", "\n");
    private XmlSerializer serializer;

    public XmlWriter(XmlSerializer serializer) {
        mXmlConvert = new XmlConvert(null);
        this.serializer = serializer;
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

    private void toXml(Element element, OutputStream outputStream, String encoding)
            throws IOException {
        if (outputStream == null) return;
//        if (IXml.DEBUG)
//            Log.d("xml", "to " + element);
        if (encoding == null) {
            encoding = IXml.DEF_ENCODING;
        }
        serializer.setOutput(outputStream, encoding);
        serializer.startDocument(encoding, null);
        if (SPACE)
            serializer.text(NEW_LINE);
        writeTag(element, serializer, 1);
        serializer.endDocument();
    }

    @SuppressWarnings("unchecked")
    private void writeTag(Element element, XmlSerializer serializer, int depth)
            throws IOException {
        if (element == null || serializer == null) return;
        if (SPACE) {
            serializer.text(NEW_LINE);
            writeTab(serializer, depth);
        }
        serializer.startTag(null, element.getName());
        for (Map.Entry<String, String> e : element.getAttributes().entrySet()) {
            serializer.attribute(null, e.getKey(), e.getValue());
        }
        serializer.text(element.getText() == null ? "" : element.getText());
        int count = element.size();
        for (int i = 0; i < count; i++) {
            writeTag(element.get(i), serializer, depth + 1);
        }
        serializer.endTag(null, element.getName());
    }

    private void writeTab(XmlSerializer pXmlSerializer, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            pXmlSerializer.text("\t");
        }
    }
}
