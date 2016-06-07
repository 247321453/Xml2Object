package com.uutils.xml2object;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElement;

import java.util.Arrays;

public class Fri {
    @XmlAttribute("name1")
    String name;

//    @XmlElement("address")
    String address;

    @XmlElement("man")
    public Man mMan;

    @XmlElement(value = "test", isString = true)
    int[] test;

    @Override
    public String toString() {
        return "Fri{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", mMan=" + mMan +
                ", test=" + Arrays.toString(test) +
                '}';
    }
}
