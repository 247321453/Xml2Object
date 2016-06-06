package com.uutils.xml2object;

import net.kk.xml.annotations.XmlElement;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

public class EnumTest {
    static enum A {
        a(2), b(1);
        private int val;

        private A(int v) {
            val = v;
        }

        private A(String v) {
            val = Integer.valueOf(v);
        }

        public int value() {
            return val;
        }
    }

    static enum B {
        a("b"), b("a");
        private String val;

        private B(String v) {
            val = v;
        }

        public String value() {
            return val;
        }
    }
    static class C{
        @XmlElement("a")
        A a;

        @Override
        public String toString() {
            return "C{" +
                    "a=" + a +
                    '}';
        }
    }

    @Test
    public void testNum() throws XmlPullParserException {
        String xml = "<c><a>a</a></c>";
//        XmlReader xmlReader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser());
//        C a = null;
//        try {
//            a = xmlReader.from(new ByteArrayInputStream(xml.getBytes()), C.class, null);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        System.out.print(a);
    }
}
