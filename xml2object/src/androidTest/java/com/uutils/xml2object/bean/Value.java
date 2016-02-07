package com.uutils.xml2object.bean;

import org.xml.annotation.XmlElementText;

/**
 * Created by Hasee on 2016/2/4.
 */
public class Value<T> extends When<T> {

    @XmlElementText
    protected T value;

    public T getValue() {
        return value;
    }

    public Value() {

    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
}
