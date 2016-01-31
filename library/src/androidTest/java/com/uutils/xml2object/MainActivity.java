package com.uutils.xml2object;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.xml.bean.Tag;
import org.xml.core.XmlConvert;
import org.xml.core.XmlReader;
import org.xml.core.XmlWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test();
    }

    private void test() {
        Man man1 = new Man();
        man1.name = "man1";
        man1.date = "20160130";
        man1.maps.put("encrypt", 1);
        man1.maps.put("decrypt", 2);
        man1.text = "hello";
        man1.sons = new ArrayList<>();
        Son son = new Son();
        son.name = "son name";
        son.phone = "13800138000";
        son.mFri = new Fri();
        son.mFri.name = "fri name<>";
        son.mFri.address = "地址";
        man1.as[0] = 999;
        man1.as[1] = -1;
        man1.sons.add(son);
        man1.sons.add(new Son());
        XmlReader xmlReader = new XmlReader();
        XmlWriter xmlWriter = new XmlWriter();
        XmlConvert convert = new XmlConvert();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Tag tag = convert.toTag(man1, null);
            xmlWriter.toXml(tag, outputStream, null);
            Log.d("xml", "" + tag);
            String xmlStr = outputStream.toString();
            Log.i("xml", "" + xmlStr);
            Tag tag1 = convert.toTag(Man.class, new ByteArrayInputStream(xmlStr.getBytes()));
            outputStream = new ByteArrayOutputStream();
            Log.d("xml", "" + tag1);
            xmlWriter.toXml(tag1, outputStream, null);
            xmlStr = outputStream.toString();
            Log.i("xml", "" + xmlStr);
            Man m = xmlReader.fromTag(tag1, Man.class);
            Log.i("xml", "main=" + m);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("xml", "" + e.getCause());
        }
    }
}
