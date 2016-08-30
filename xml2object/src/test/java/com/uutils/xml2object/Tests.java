package com.uutils.xml2object;

import net.kk.xml.XmlOptions;
import net.kk.xml.XmlReader;
import net.kk.xml.XmlWriter;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Tests {

    @Test
    public void testType() {
        System.out.println(Boolean.class.isAssignableFrom(boolean.class));
        System.out.println(boolean.class.isAssignableFrom(Boolean.class));
    }

    @Test
    public void getType() {
        List<String> list = new ArrayList<String>();
        System.out.println(list.getClass().getGenericSuperclass());
        System.out.println(Arrays.toString(list.getClass().getGenericInterfaces()));
    }

    @Test
    public void testbean() throws XmlPullParserException {
        TestBean testBean = new TestBean();
        testBean.age = 18;
        testBean.name = "hi";
        testBean.as = new int[]{12, 123,14};
        testBean.i = 990L;
        testBean.hello = false;
        testBean.maps = new HashMap<String, Integer>();
        testBean.maps.put("encrypt", 1);
        testBean.maps.put("decrypt", 2);
        XmlOptions options = new XmlOptions.Builder()
                //忽略没有注解的字段
//                .ignoreNoAnnotation()
                .dontUseSetMethod()
                //list，map的元素在同一级
//                .enableSameAsList()
                .useSpace()
                .registerTypeAdapter(PeopleType.class, new PeopleTypeAdapter())
                .registerTypeAdapter(Boolean.class, new BooleanAdapter())
                .registerTypeAdapter(int[].class, new IntegerArrayAdapter())
                .build();
        XmlReader xmlReader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), options);
        XmlWriter xmlWriter = new XmlWriter(XmlPullParserFactory.newInstance().newSerializer(), options);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            long time1 = System.currentTimeMillis();
            System.out.println("main=" + testBean + "\n");
            xmlWriter.toXml(testBean, outputStream, null);
            System.out.println("time1=" + (System.currentTimeMillis() - time1));
            String xmlStr = outputStream.toString();
            System.out.println("" + xmlStr + "\n");
            time1 = System.currentTimeMillis();
            TestBean testBean2 = xmlReader.from(new ByteArrayInputStream(xmlStr.getBytes()), TestBean.class, null);
            System.out.println("time3=" + (System.currentTimeMillis() - time1));
            System.out.println("main=" + testBean2 + "\n");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws XmlPullParserException {
        Man man1 = new Man();
        man1.initSub();
        man1.sons = new ArrayList<Son>();
        man1.maps = new HashMap<String, Integer>();
        man1.name = "man1";
        man1.hello = true;
        man1.type = PeopleType.Woman;
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
        son.mFri.test = new int[3];
        man1.as[0] = 998;
        man1.as[1] = -1;
        man1.sons.add(son);
        man1.sons.add(new Son());
//        man1.childs=new ArrayList<>();
//        man1.childs.addChild(new Man("join", 16));
//        man1.childs.addChild(new Woman("lily", 18));
        XmlOptions options = new XmlOptions.Builder()
//                .ignoreNoAnnotation()
                .dontUseSetMethod()
                //list，map的元素在同一级
//                .enableSameAsList()
                .useSpace()
                .registerTypeAdapter(PeopleType.class, new PeopleTypeAdapter())
                .registerTypeAdapter(Boolean.class, new BooleanAdapter())
                .registerTypeAdapter(int[].class, new IntegerArrayAdapter())
                                .ignore(Son.class)
                .build();
        XmlReader xmlReader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), options);
        XmlWriter xmlWriter = new XmlWriter(XmlPullParserFactory.newInstance().newSerializer(), options);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            long time1 = System.currentTimeMillis();
            System.out.println("main=" + man1 + "\n");
            xmlWriter.toXml(man1, outputStream, null);
            System.out.println("time1=" + (System.currentTimeMillis() - time1));
            String xmlStr = outputStream.toString();
            System.out.println("" + xmlStr + "\n");
            time1 = System.currentTimeMillis();
            Man m = xmlReader.from(new ByteArrayInputStream(xmlStr.getBytes()), Man.class, null);
            System.out.println("time3=" + (System.currentTimeMillis() - time1));
            System.out.println("main=" + m + "\n");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
