package com.uutils.xml2object;

import org.xml.annotation.XmlAttribute;
import org.xml.annotation.XmlElement;
import org.xml.annotation.XmlElementList;
import org.xml.annotation.XmlElementMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@XmlElement("man")
public class Man implements IPeople {
    public Man() {
        // maps = new HashMap<>();
        // sons = new ArrayList<>();
    }

    public Man(String pName, int pAge) {
        age = pAge;
        name = pName;
    }

    @XmlAttribute("name1")
    String name;

    @XmlAttribute("sex")
    String sex = "man";

    @XmlAttribute("age")
    int age;
    @XmlAttribute("hello")
    Boolean hello;
    @XmlElement("as")
    int[] as = new int[2];
    @XmlElement("i")
    private Long i = 2L;

    public long getI() {
        return i;
    }

    public void setI(Long i) {
        System.out.println("set " + i);
        this.i = 999L;
    }
//    @XmlElement(value = "child", type = PeopleCreator.class)
//    List<Man> childs;

    @XmlAttribute("type")
    PeopleType type = PeopleType.Man;

    @XmlElementList(value = "son1",item = "son" ,type = Son.class)
    List<Son> sons;

    @XmlElementMap(value = "maps", keyType = String.class, valueType = Integer.class)
    Map<String, Integer> maps;

    @Override
    public String toString() {
        return "Man{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", as=" + Arrays.toString(as) +
                ", i=" + i +
                ", type=" + type +
                ", sons=" + sons +
                ", maps=" + maps +
                '}';
    }

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
        return 0;
    }
}
