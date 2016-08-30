package net.kk.xml.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Created by Administrator on 2016/8/30.
 */
public class XmlConstructors {
    public XmlConstructorAdapter objectXmlConstructorAdapter = new XmlConstructorAdapter() {
        @Override
        public <T> T create(Class<T> pClass, Object parent) {
            T o = null;
            try {
                if (pClass.isMemberClass() && (pClass.getModifiers() & Modifier.STATIC) == 0) {
                    //内部类
                    o = (T) Reflect.create(pClass, new Class[]{parent.getClass()}, new Object[]{parent});
                } else {
                    o = Reflect.create(pClass, null, null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return o;
        }
    };
}
