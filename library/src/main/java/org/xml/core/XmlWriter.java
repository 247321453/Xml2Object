package org.xml.core;

import android.util.Xml;

import org.xml.bean.Tag;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class XmlWriter extends IXml {
    public void toXml(Tag tag, OutputStream outputStream)
            throws IOException, IllegalAccessException {
        toXml(tag, outputStream, "UTF-8");
    }

    public void toXml(Tag tag, OutputStream outputStream, String encoding)
            throws IOException, IllegalAccessException {
        if (outputStream == null) return;
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outputStream, encoding);
        serializer.startDocument(encoding, null);
        writeTag(tag, serializer);
        serializer.endDocument();
    }

    @SuppressWarnings("unchecked")
    private void writeTag(Tag tag, XmlSerializer serializer)
            throws IllegalAccessException, IOException {
        if (tag == null || serializer == null) return;
        serializer.startTag(null, tag.name);
        for (Map.Entry<String, String> e : tag.attributes.entrySet()) {
            serializer.attribute(null, e.getKey(), e.getValue());
        }
        serializer.text(tag.value);
        for (Tag t : tag.tags) {
            writeTag(t, serializer);
        }
        serializer.endTag(null, tag.name);
    }
}
