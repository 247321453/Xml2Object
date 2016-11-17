package net.kk.xml.internal;

import net.kk.xml.core.Reflect;

import java.lang.reflect.Modifier;

public class XmlConstructors {
    public XmlConstructorAdapter objectXmlConstructorAdapter = new XmlConstructorAdapter() {
        @Override
        public <T> T create(Class<T> pClass, Object parent) {
            T o = null;
            try {
                if (pClass.isMemberClass() && (pClass.getModifiers() & Modifier.STATIC) == 0) {
                    //内部类
                    o = Reflect.get(pClass).create(new Class[]{parent.getClass()}, new Object[]{parent});
                } else {
                    o = Reflect.get(pClass).create(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return o;
        }
    };
}
