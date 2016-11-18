package net.kk.xml.v2.adapter;

public interface XmlTextAdapter<T> {
    T toObject(Class<?> tClass, String val,Object parent) throws Exception;

    String toString(Class<T> tClass,T var);
}
