package com.uutils.xml2object;

import org.junit.Test;
import org.xml.core.XmlReader;
import org.xml.core.XmlWriter;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Tests {

    @Test
    public void test() throws XmlPullParserException {
        Man man1 = new Man();
        man1.sons = new ArrayList<Son>();
        man1.maps = new HashMap<String, Integer>();
        man1.name = "man1";
        man1.hello = true;
        man1.maps.put("encrypt", 1);
        man1.maps.put("decrypt", 2);
        man1.age = 20;
        man1.setI(4L);
        Son son = new Son();
        son.name = "son name";
        son.phone = "13800138000";
        son.mFri = new Fri();
        son.mFri.name = "fri name<>";
        son.mFri.address = "地址";
        son.mFri.mMan = new Man("a", 21);
        man1.as[0] = 998;
        man1.as[1] = -1;
        man1.sons.add(son);
        man1.sons.add(new Son());
//        man1.childs=new ArrayList<>();
//        man1.childs.add(new Man("join", 16));
//        man1.childs.add(new Woman("lily", 18));
        XmlReader xmlReader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser());
        xmlReader.setSameAsList(false);
        xmlReader.setUseSetMethod(false);
        XmlWriter xmlWriter = new XmlWriter(XmlPullParserFactory.newInstance().newSerializer());
        xmlWriter.setSameAsList(false);
        xmlWriter.setUseSpace(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            System.out.println("main=" + man1 + "\n");
            xmlWriter.toXml(man1, outputStream, null);
            String xmlStr = outputStream.toString();
            System.out.println("" + xmlStr + "\n");
            Man m = xmlReader.from(new ByteArrayInputStream(xmlStr.getBytes()), Man.class, null);
            System.out.println("main=" + m + "\n");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
