package com.uutils.xml2object.bean;

import org.xml.annotation.XmlAttribute;

public  class IName {

    @XmlAttribute("name")
    protected String name;

    public String getName() {
        return name;
    }
}
