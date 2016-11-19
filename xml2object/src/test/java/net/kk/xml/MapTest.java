package net.kk.xml;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElementText;
import net.kk.xml.annotations.XmlElement;
import net.kk.xml.bean.TagObject;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;


public class MapTest {

    static class A {
        public A(String name) {
            this.name = name;
            list = new LinkedHashMap<>();
        }

        @XmlElement("bs")
        Map<Integer, B> list;
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

        @XmlElementText
        public String text;

        @Override
        public String toString() {
            return "C{" + text + "}";
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
        a.list.put(10, new B(10, "0"));
        a.list.put(11, new B(11, "1"));
        System.out.println(a);
        XmlWriter writer = new XmlWriter(XmlPullParserFactory.newInstance().newSerializer(), DEFAULT);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TagObject root = writer.toRootTag(a);
        System.out.println("\r" + root);
        writer.write(root, outputStream, null);
        String xml = outputStream.toString();
        System.out.println("\r" + xml);

        XmlReader reader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), DEFAULT);
        root = reader.parseTags(new ByteArrayInputStream(xml.getBytes()), null);
        System.out.println("\r" + root);
        a = reader.fromTag(A.class, root);
        System.out.println("\r" + a);
        System.out.println(a.list.getClass());
    }
}
