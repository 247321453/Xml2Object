package com.uutils.xml2object.bean;

import android.text.TextUtils;

import org.xml.annotation.XmlAttribute;

public abstract class When<T>{
    @XmlAttribute("when")
    protected String when;

    public String getWhen() {
        return when;
    }

    public boolean hasWhen() {
        return !TextUtils.isEmpty(when);
    }

    public abstract T get();

    @Override
    public String toString() {
        return "When{" +
                "when='" + when + '\'' +
                '}';
    }
}
