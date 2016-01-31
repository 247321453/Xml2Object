package org.xml.core;

import android.util.Xml;

import org.xml.bean.Tag;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * {@link org.xml.bean.Tag } 转文件
 */
public class XmlWriter {
    static final String DEF_ENCODING = "UTF-8";

    /***
     * @param tag          tag对象 {@link org.xml.core.XmlConvert().toTag }
     * @param outputStream 输出流
     * @param encoding     xml编码
     * @throws IOException io异常
     */
    public static void toXml(Tag tag, OutputStream outputStream, String encoding)
            throws IOException {
        if (outputStream == null) return;
        XmlSerializer serializer = Xml.newSerializer();
        if (encoding == null) {
            encoding = DEF_ENCODING;
        }
        serializer.setOutput(outputStream, encoding);
        serializer.startDocument(encoding, null);
        writeTag(tag, serializer);
        serializer.endDocument();
    }

    @SuppressWarnings("unchecked")
    private static void writeTag(Tag tag, XmlSerializer serializer)
            throws IOException {
        if (tag == null || serializer == null) return;
        serializer.startTag(null, tag.getName());
        for (Map.Entry<String, String> e : tag.attributes.entrySet()) {
            serializer.attribute(null, e.getKey(), e.getValue());
        }
        serializer.text(tag.getValue());
        int count = tag.size();
        for (int i = 0; i < count; i++) {
            writeTag(tag.get(i), serializer);
        }
        serializer.endTag(null, tag.getName());
    }
}
