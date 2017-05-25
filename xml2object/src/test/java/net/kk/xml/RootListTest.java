package net.kk.xml;

import net.kk.xml.annotations.XmlElement;
import net.kk.xml.annotations.XmlElementText;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;

public class RootListTest {
    @XmlElement("a")
    static class A{
        @XmlElementText
        public String name;

        @Override
        public String toString() {
            return "A{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
    static class AList{
        @XmlElement("a")
        List<A> as;

        @Override
        public String toString() {
            return "AList{" +
                    "as=" + as +
                    '}';
        }
    }
    @Test
    public void test() throws Exception {
        XmlOptions DEFAULT = new XmlOptions.Builder()
                .enableSameAsList()
                .ignoreNoAnnotation()
                .dontUseSetMethod()
                .useSpace()
                .build();
        String xml = "<as><a>1</a><a>2</a><a>3</a><a>4</a></as>";
        XmlReader reader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), DEFAULT);
        AList as = reader.fromXml(AList.class, xml);
        System.out.println(as);
    }
}
