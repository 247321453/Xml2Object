package com.uutils.xml2object.bean;

import org.xml.annotation.XmlElement;
import org.xml.annotation.XmlElementArray;

import java.util.ArrayList;
import java.util.List;

@XmlElement("styleinfo")
public class StyleInfo {
    @XmlElement("name")
    private String name = "style name";
    @XmlElement("desc")
    private String desc;
    @XmlElement("author")
    private String author = "author name";
    @XmlElement("version")
    private int version = 0;
    @XmlElement("url")
    private String url;
    @XmlElement("icon")
    private String icon;
    @XmlElement("data")
    private String data;

    @XmlElementArray(value = "font", type = FontElement.class)
    private final List<FontElement> mFontElements;

    public StyleInfo() {
        mFontElements = new ArrayList<>();
    }

    public List<FontElement> getFontElements() {
        return mFontElements;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "StyleInfo{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", author='" + author + '\'' +
                ", version=" + version +
                ", url='" + url + '\'' +
                ", mFontElements=" + mFontElements +
                '}';
    }
}
