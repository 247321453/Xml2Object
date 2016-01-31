package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlTag("man")
public class Man {
    public Man() {
        maps = new HashMap<>();
    }

    @XmlAttribute("name1")
    String name;

    @XmlAttribute("date1")
    String date;

    @XmlTag(value = "son1", valueType = Son.class)
    List<Son> sons;

    @XmlTag(value = "maps", keyType = Integer.class, valueType = String.class)
    final Map<String, Integer> maps;

    @Override
    public String toString() {
        return "Man{" +
                " name='" + name + '\'' +
                ", son=" + sons +
                '}';
    }
}
