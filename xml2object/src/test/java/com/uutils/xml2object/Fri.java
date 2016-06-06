package com.uutils.xml2object;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElement;

public class Fri {
    @XmlAttribute("name1")
    String name;

    @XmlElement("address")
    String address;

    @XmlElement ("man")
    public Man mMan;

    @Override
    public String toString() {
        return "Fri{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", mMan=" + mMan +
                '}';
    }
}
