package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;

/**
 * Created by Hasee on 2016/2/2.
 */
public class Woman implements IPeople {
    public Woman(String pName,int pAge) {
        age = pAge;
        name = pName;
    }
    @XmlAttribute("name")
    String name;

    @XmlAttribute("sex")
    String sex="woman";

    @XmlAttribute("age")
    int age;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSex() {
        return sex;
    }

    @Override
    public int getAge() {
        return age;
    }
}
