package org.xml.core;

import android.util.Log;

import org.xml.bean.Tag;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlReader extends IXml {
    public static Tag read(InputStream inputStream) {
        if (inputStream == null) return null;
        Tag main = new Tag();
        XmlPullParser xmlParser = android.util.Xml.newPullParser();
        List<Tag> tagList = new ArrayList<>();
        int depth = -1;
        try {
            Tag tmp = null;
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            // 一直循环，直到文档结束
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        //属性
                        String tag = xmlParser.getName();
                        int d = xmlParser.getDepth();
                        tmp = new Tag();
                        Tag parent = tagList.get(d - 1);
                        if (parent != null) {
                            parent.tags.add(tmp);
                        }

                        Log.v("xml", "depth=" + d);
                        int count = xmlParser.getAttributeCount();
                        Log.d("xml", "set attribute " + tag);
                        for (int i = 0; i < count; i++) {
                            String k = xmlParser.getAttributeName(i);
                            String v = xmlParser.getAttributeValue(i);
                            tmp.attributes.put(k, v);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (tmp != null) {
                            tmp.value = xmlParser.getText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 如果xml没有结束，则导航到下一个river节点
                evtType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e) {

                }
            }
        }
        return main;
    }
}
