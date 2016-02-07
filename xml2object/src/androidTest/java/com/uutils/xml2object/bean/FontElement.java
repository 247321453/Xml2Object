package com.uutils.xml2object.bean;

import org.xml.annotation.XmlAttribute;

public class FontElement extends IName {
    /**
     * 默认值
     */
    @XmlAttribute("src")
    protected String src = "";

    public String getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return "FontElement{" +
                "src='" + src + '\'' +
                ", name='" + name + "'" +
                '}';
    }
}
