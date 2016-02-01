package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlElement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@XmlElement("man")
public class Man {
    public Man() {
      //  maps = new HashMap<>();
     //   sons = new ArrayList<>();
    }

    @XmlAttribute("name1")
    String name;

    @XmlAttribute("date1")
    String date;

    @XmlElement("text")
    String text;

    int[] as = new int[2];
    @XmlElement(value = "son1", type = Son.class)
    List<Son> sons;

    @XmlElement(value = "maps", keyType = String.class, valueType = Integer.class)
    Map<String, Integer> maps;

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
