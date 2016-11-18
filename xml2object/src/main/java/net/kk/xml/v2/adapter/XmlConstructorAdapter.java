package net.kk.xml.v2.adapter;

public interface XmlConstructorAdapter {
    <T> T create(Class<T> tClass,Object parent);
}
