package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;

/**
 * Created by Administrator on 2016/1/30.
 */
@XmlTag("man")
public class Man {
    public Man() {

    }

    @XmlAttribute("name1")
    String name;

    @XmlAttribute("date1")
    String date;

    @XmlTag("son1")
    Son son;

    @Override
    public String toString() {
        return "Man{" +
                " name='" + name + '\'' +
                ", son=" + son +
                '}';
    }
}
