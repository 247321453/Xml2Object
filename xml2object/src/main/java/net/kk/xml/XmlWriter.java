package net.kk.xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * {@link XmlObject } 转文件
 */
public class XmlWriter extends XmlBase {
    private static final String NEW_LINE = System.getProperty("line.separator", "\n");
    private XmlSerializer serializer;
    private XmlObjectWriter mXmlObjectWriter;

    public XmlWriter(XmlSerializer serializer, XmlOptions options) {
        super(options);
        this.mXmlObjectWriter = new XmlObjectWriter(this);
        this.serializer = serializer;
    }

    /***
     * @param object       java对象
     * @param outputStream 输出流
     * @param encoding     xml编码
     * @throws IOException            io异常
     * @throws IllegalAccessException 类型异常
     */
    @SuppressWarnings("unchecked")
    public void toXml(Object object, OutputStream outputStream, String encoding)
            throws Exception {
        if (object == null) return;
        Class cls = object.getClass();
        String name = getTagName(cls);
        long time = System.currentTimeMillis();
        XmlObject xmlObject = mXmlObjectWriter.toObject(name, cls, object);
        if (DEBUG) {
            System.out.println("time0=" + (System.currentTimeMillis() - time));
        }
        if (xmlObject == null) {
            xmlObject = new XmlObject(name);
        }
        toXml(xmlObject, outputStream, encoding);
    }

    private void toXml(XmlObject xmlObject, OutputStream outputStream, String encoding)
            throws IOException, IllegalAccessException {
        if (outputStream == null) return;
        if (encoding == null) {
            encoding = DEF_ENCODING;
        }
        serializer.setOutput(outputStream, encoding);
        serializer.startDocument(encoding, null);
        writeTag(xmlObject, serializer, 1);
        serializer.endDocument();
    }

    @SuppressWarnings("unchecked")
    private void writeTag(XmlObject xmlObject, XmlSerializer serializer, int depth)
            throws IOException, IllegalAccessException {
        if (xmlObject == null || serializer == null) return;
        //list,map,array
        boolean noSameList = isNormal(xmlObject.getType()) || xmlObject.isSubItem() || !(mOptions.isSameAsList() && isArray(xmlObject.getTClass()));
        if (noSameList) {
            if (mOptions.isUseSpace()) {
                serializer.text(NEW_LINE);
                writeTab(serializer, depth);
            }
            serializer.startTag(null, xmlObject.getName());
            if (xmlObject.getAttributes() != null) {
                for (Map.Entry<String, String> e : xmlObject.getAttributes().entrySet()) {
                    serializer.attribute(null, e.getKey(), e.getValue());
                }
            }
            serializer.text(xmlObject.getText() == null ? "" : xmlObject.getText());
        }
        int count = xmlObject.size();
        for (int i = 0; i < count; i++) {
            writeTag(xmlObject.get(i), serializer, noSameList ? depth + 1 : depth);
        }
        if (noSameList) {
            if (count > 0 && mOptions.isUseSpace()) {
                serializer.text(NEW_LINE);
                writeTab(serializer, depth);
            }
            serializer.endTag(null, xmlObject.getName());
        }
    }

    private void writeTab(XmlSerializer pXmlSerializer, int depth) throws IOException {
        for (int i = 0; i < depth - 1; i++) {
            pXmlSerializer.text("\t");
        }
    }
}
