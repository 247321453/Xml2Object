package com.uutils.xml2object;

import net.kk.xml.annotations.XmlAttribute;
import net.kk.xml.annotations.XmlElement;
import net.kk.xml.annotations.XmlElementList;
import net.kk.xml.annotations.XmlElementMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlElement("man")
public class Man implements IPeople {
    public Man(String name) {
        this.name = name;
        // maps = new HashMap<>();
        // sons = new ArrayList<>();
    }

    public void initSub() {
        sub = new SubClass();
        subs = new ArrayList<SubClass>();
        subs.add(new SubClass());
        subMap = new HashMap<String, SubClass>();
        subMap.put("1", new SubClass());
    }

    public Man(String pName, int pAge) {
        age = pAge;
        name = pName;
    }

    class SubClass {
        @XmlElement("a")
        int a;

        @Override
        public String toString() {
            return "sub[a=" + a + "]";
        }
    }

    @XmlElement("inner")
    SubClass sub;
    @XmlElementList("inners")
    List<SubClass> subs;

    @XmlElementMap("innermap")
    Map<String, SubClass> subMap;

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

    @XmlElementList(value = "son1", item = "son")
    List<Son> sons;

    @XmlElementMap(value = "maps")
    Map<String, Integer> maps;

    @Override
    public String toString() {
        return "Man{" +
                "age=" + age +
                ", sub=" + sub +
                ", subs=" + subs +
                ", innermap=" + subMap +
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
