package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlElement;
import org.xml.annotation.XmlElementText;

public class Fri {
    @XmlAttribute("name1")
    String name;

    @XmlElementText
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
