package net.kk.xml;

import net.kk.xml.annotations.XmlElement;
import net.kk.xml.annotations.XmlIgnore;
import net.kk.xml.bean.TagObject;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;

public class StyleInfoTest {
    static class StyleInfo {
        @XmlElement("name")
        private String name;
        @XmlElement("desc")
        private String desc;
        @XmlElement("author")
        private String author;
        @XmlElement("version")
        private long version = 0;
        @XmlElement("app-version")
        private long appversion = -1;
        @XmlElement("icon")
        private String icon;

        /**
         * style file path
         */
        @XmlElement("url")
        private String filepath;

        @XmlIgnore
        private String stylePath;
        @XmlIgnore
        private String dataPath;

        public String getFilepath() {
            return filepath;
        }

        public StyleInfo() {
            author = "author name";
            name = "style name";
        }

        @Override
        public String toString() {
            return "StyleInfo{" +
                    "name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", author='" + author + '\'' +
                    ", version=" + version +
                    ", appversion=" + appversion +
                    ", icon='" + icon + '\'' +
                    ", filepath='" + filepath + '\'' +
                    ", stylePath='" + stylePath + '\'' +
                    ", dataPath='" + dataPath + '\'' +
                    '}';
        }
    }

    @Test
    public void test() throws Exception {
        String xml = "\t<styleinfo>\n" +
                "\t\t<name>yugioh-9-cn</name>\n" +
                "\t\t<desc>游戏王第九期</desc>\n" +
                "\t\t<author>菜菜</author>\n" +
                "\t\t<version>20160222</version>\n" +
                "\t\t<app-version>1</app-version>\n" +
                "\t\t<icon>icon.png</icon>\n" +
                "\t\t<url>yugioh-9.zip</url>\n" +
                "\t</styleinfo>\n";
        XmlOptions DEFAULT = new XmlOptions.Builder()
                .enableSameAsList()
                .ignoreNoAnnotation()
                .dontUseSetMethod()
                .useSpace()
                .build();
        XmlReader reader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), DEFAULT);
        TagObject root = reader.parseTags(new ByteArrayInputStream(xml.getBytes()), null);
        System.out.println("\r" + root);
        StyleInfo a = reader.fromTag(StyleInfo.class, root);
        System.out.println("\r" + a);
    }
}
