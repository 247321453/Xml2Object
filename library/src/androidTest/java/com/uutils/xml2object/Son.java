package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;

/**
 * Created by Administrator on 2016/1/30.
 */
public class Son {
    public Son() {

    }

    @XmlAttribute("name")
    String name;

    @XmlTag("phone")
    String phone;

    @XmlTag("fri")
    Fri mFri;

    @Override
    public String toString() {
        return "Son{" +
                "mFri=" + mFri +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
