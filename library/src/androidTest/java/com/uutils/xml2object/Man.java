package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;

/**
 * Created by Administrator on 2016/1/30.
 */
public class Man {
    public Man() {

    }

    @XmlAttribute("name")
    String name;

    @XmlAttribute("date")
    String date;

    @XmlTag("son")
    Son son;

    @Override
    public String toString() {
        return "Man{" +
                " name='" + name + '\'' +
                ", son=" + son +
                '}';
    }
}
