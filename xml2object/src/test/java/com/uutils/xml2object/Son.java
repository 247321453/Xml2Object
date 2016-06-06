package com.uutils.xml2object;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElement;

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
                ", phone='" + (phone == null ? "" : phone) + '\'' +
                '}';
    }
}
