package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlElement;

public class Son {
    public Son() {
    }

    @XmlAttribute("name1")
    String name;

    @XmlElement("phone1")
    String phone;

    @XmlElement("fri1")
    Fri mFri;

    @Override
    public String toString() {
        return "Son{" +
                "mFri=" + mFri +
                ", name='" + (name == null ? "" : name) + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
