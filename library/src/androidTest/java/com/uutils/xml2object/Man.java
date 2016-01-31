package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlTag("man")
public class Man {
    public Man() {
        maps = new HashMap<>();
        sons = new ArrayList<>();
    }

    @XmlAttribute("name1")
    String name;

    @XmlAttribute("date1")
    String date;

    @XmlTag("text")
    String text;

    int[] as = new int[2];
    @XmlTag(value = "son1", type = Son.class)
    List<Son> sons;

    @XmlTag(value = "maps", keyType = String.class, valueType = Integer.class)
    final Map<String, Integer> maps;

    @Override
    public String toString() {
        return "Man{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", text='" + text + '\'' +
                ", as=" + Arrays.toString(as) +
                ", sons=" + sons +
                ", maps=" + maps +
                '}';
    }
}
