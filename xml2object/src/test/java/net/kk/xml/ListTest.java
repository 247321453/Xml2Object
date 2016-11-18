package net.kk.xml;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlInnerText;
import net.kk.xml.bean.TagObject;
import net.kk.xml.annotations.XmlElement;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ListTest {

    static class A {
        public A(String name) {
            this.name = name;
        }

        @XmlElement("bs")
        List<B> list;
        @XmlAttribute("name")
        String name;

        @Override
        public String toString() {
            return "A{" +
                    "list=" + list +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    static class B {
        public B(int id, String name) {
            this.id = id;
            this.name = new C(name);
        }

        @XmlAttribute("id")
        int id;

        @XmlElement("c")
        C name;

        @Override
        public String toString() {
            return "B{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    static class C {
        public C(String text) {
            this.text = text;
        }

        @XmlInnerText
        public String text;

        @Override
        public String toString() {
            return "C{"+text+"}";
        }
    }

    @Test
    public void test() throws Exception {
        XmlOptions DEFAULT = new XmlOptions.Builder()
//                .enableSameAsList()
                .ignoreNoAnnotation()
                .dontUseSetMethod()
                .useSpace()
                .build();
        test(DEFAULT);
    }

    @Test
    public void testSame() throws Exception {
        XmlOptions DEFAULT = new XmlOptions.Builder()
                .enableSameAsList()
                .ignoreNoAnnotation()
                .dontUseSetMethod()
                .useSpace()
                .build();
        test(DEFAULT);
    }

    private void test(XmlOptions DEFAULT) throws Exception {
        A a = new A("a");
        a.list = new ArrayList<>();
        a.list.add(new B(10, "0"));
        a.list.add(new B(11, "1"));
        System.out.println(a);
        XmlWriter writer = new XmlWriter(XmlPullParserFactory.newInstance().newSerializer(), DEFAULT);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TagObject root = writer.toRootTag(a);
        System.out.println(root);
        writer.write(root, outputStream, null);
        String xml = outputStream.toString();
        System.out.println(xml);

        XmlReader reader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), DEFAULT);
        a = reader.fromXml(A.class, xml);
        System.out.println(a);
    }
}
