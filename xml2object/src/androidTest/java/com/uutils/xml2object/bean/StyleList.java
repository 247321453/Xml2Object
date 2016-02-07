package com.uutils.xml2object.bean;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

@XmlElement("style-list")
public class StyleList {
    @XmlAttribute("date")
    public long date;

    @XmlElement(value = "styleinfo", type = StyleInfo.class)
    public final List<StyleInfo> mStyleInfos;

    @XmlElement("datetime")
    public Value<Long> datetime;

    public StyleList() {
        mStyleInfos = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "StyleList{" +
                "date=" + date +
                ", mStyleInfos=" + mStyleInfos +
                ", datetime=" + datetime +
                '}';
    }
}
