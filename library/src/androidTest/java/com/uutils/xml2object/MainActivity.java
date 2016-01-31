package com.uutils.xml2object;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.xml.bean.Tag;
import org.xml.core.XmlReader;
import org.xml.core.XmlWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Man man1 = new Man();
        man1.name = "man1";
        man1.date = "20160130";
        man1.maps.put("a", 1);
        man1.maps.put("b", 2);
        man1.sons = new ArrayList<>();
        Son son = new Son();
        son.name = "son name";
        son.phone = "13800138000";
        son.mFri = new Fri();
        son.mFri.name = "fri name<>";
        son.mFri.address = "地址";
        man1.sons.add(son);
        man1.sons.add(new Son());
        XmlReader xmlReader = new XmlReader();
        XmlWriter xmlWriter = new XmlWriter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Tag tag = xmlWriter.toTag(man1, null);
            xmlWriter.toXml(tag, outputStream, null);
            String xmlStr = outputStream.toString();
            Log.i("xml", "" + xmlStr);
            Tag tag1 = xmlReader.read(Man.class, new ByteArrayInputStream(xmlStr.getBytes()));
            outputStream = new ByteArrayOutputStream();
            xmlWriter.toXml(tag1, outputStream, null);
            Log.i("xml", "" + outputStream.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("xml", "" + e.getCause());
        }
    }
}
