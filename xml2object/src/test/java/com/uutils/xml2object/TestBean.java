package com.uutils.xml2object;

import net.kk.xml.annotations.XmlElement;

import java.util.Arrays;
import java.util.Map;

@XmlElement("test")
public class TestBean {
    String name;

    String sex = "man";
    int age;
    Boolean hello;
    int[] as = new int[2];
    Long i = 2L;
    Map<String, Integer> maps;

    @Override
    public String toString() {
        return "TestBean{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", hello=" + hello +
                ", as=" + Arrays.toString(as) +
                ", i=" + i +
                ", maps=" + maps +
                '}';
    }
}
