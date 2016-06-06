package net.kk.xml.internal;

import net.kk.xml.XmlReader;
import net.kk.xml.XmlWriter;

import java.lang.reflect.AnnotatedElement;

public abstract interface XmlTypeAdapter<T> {

    /***
     *
     * @param writer writer
     * @param name 名字
     * @param element field/class
     * @param object 对象，可能为null
     * @return xmlobject
     */
    XmlObject write(XmlWriter writer, String name, AnnotatedElement element, T object) throws Exception;

    /***
     *
     * @param reader reader
     * @param parent 父标签
     * @param xmlObject xmlobject
     * @param init 默认值
     * @return t
     */
    T read(XmlReader reader,XmlObject parent,XmlObject xmlObject,Object init) throws Exception;
}
