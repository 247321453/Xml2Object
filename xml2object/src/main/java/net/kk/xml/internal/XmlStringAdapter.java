package net.kk.xml.internal;

public interface XmlStringAdapter<T> {
    T toObject(Class<?> tClass, String val) throws Exception;

    String toString(Class<T> tClass, T var);
}
