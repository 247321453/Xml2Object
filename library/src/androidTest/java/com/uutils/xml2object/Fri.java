package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlValue;

public class Fri {
    @XmlAttribute("name1")
    String name;

    @XmlValue
    String address;

    @Override
    public String toString() {
        return "Fri{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
