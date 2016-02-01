package com.uutils.xml2object;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.xml.core.XmlReader;
import org.xml.core.XmlWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test();
    }

    private void test() {
        Man man1 = new Man();
        man1.sons = new ArrayList<>();
        man1.maps = new HashMap<>();
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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Log.i("xml", "main=" + man1);
            xmlWriter.toXml(man1, outputStream, null);
            String xmlStr = outputStream.toString();
            Log.i("xml", "" + xmlStr);
            Man m = xmlReader.from(new ByteArrayInputStream(xmlStr.getBytes()), Man.class);
            Log.i("xml", "main=" + m);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("xml", "" + e.getCause());
        }
    }
}
