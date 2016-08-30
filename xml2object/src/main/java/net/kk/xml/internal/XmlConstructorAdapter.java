package net.kk.xml.internal;

/**
 * Created by Administrator on 2016/8/30.
 */
public interface XmlConstructorAdapter {
    <T> T create(Class<T> tClass,Object parent);
}
