package com.uutils.xml2object;


import net.kk.xml.XmlOptions;
import net.kk.xml.XmlReader;
import net.kk.xml.XmlWriter;

import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/27.
 */
public class XmlUtils {

    private static final XmlOptions options=new XmlOptions.Builder().useSpace().dontUseSetMethod()
//            .registerConstructorAdapter(Location.class, new XmlConstructorAdapter() {
//                @Override
//                public <T> T create(Class<T> aClass, Object o) {
//                    return (T) new Location(LocationManager.GPS_PROVIDER);
//                }
//            })
            .build();

    public static String toXml(Object object) throws Exception {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        XmlWriter writer = new XmlWriter(XmlPullParserFactory.newInstance().newSerializer(), options);
        writer.toXml(object, arrayOutputStream, null);
        return new String(arrayOutputStream.toByteArray());
    }

    public static <T> T getObject(Class<T> tClass, InputStream inputStream) throws Exception {
        T t = null;
        XmlReader reader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), options);
        t = reader.from(inputStream, tClass, null, null);
        return t;
    }

    public static <T> T getObject(Class<T> tClass, String xml) throws Exception {
        if (xml == null) {
            return null;
        }
        return getObject(tClass, new ByteArrayInputStream(xml.getBytes()));
    }
}
